package com.example.polls_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.polls_service.dto.request.VoteRequest;
import com.example.polls_service.dto.response.MyVoteResponse;
import com.example.polls_service.dto.response.PollResultItemResponse;
import com.example.polls_service.dto.response.PollResultsResponse;
import com.example.polls_service.exception.BadRequestException;
import com.example.polls_service.model.Poll;
import com.example.polls_service.model.PollOption;
import com.example.polls_service.model.PollStatus;
import com.example.polls_service.model.Vote;
import com.example.polls_service.repository.PollOptionRepository;
import com.example.polls_service.repository.VoteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollService pollService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void castVote(Long pollId, VoteRequest request, UUID userId) {
        Poll poll = pollService.getPollEntity(pollId);

        if (poll.getStatus() != PollStatus.OPEN) {
            throw new BadRequestException("Poll is not open for voting");
        }

        if (!poll.isMultipleChoice() && request.getOptionIds().size() > 1) {
            throw new BadRequestException("This poll allows only a single choice.");
        }

        voteRepository.deleteByPollIdAndUserId(pollId, userId);

        List<PollOption> options = pollOptionRepository.findAllById(request.getOptionIds());
        for (PollOption option : options) {


            if (!option.getPoll().getId().equals(pollId)) {
                throw new BadRequestException("Option " + option.getId() + " does not belong to this poll");
            }

            Vote vote = Vote.builder()
                    .poll(poll)
                    .option(option)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();
            voteRepository.save(vote);
        }

        broadcastUpdate(pollId);
    }

    @Transactional
    public void cancelVote(Long pollId, UUID userId) {
        Poll poll = pollService.getPollEntity(pollId);

        if (poll.getStatus() != PollStatus.OPEN) {
            throw new BadRequestException("Cannot cancel vote in a closed/draft poll");
        }

        voteRepository.deleteByPollIdAndUserId(pollId, userId);
        broadcastUpdate(pollId);
    }

    @Transactional
    public void removeParticipantByAdmin(Long pollId, UUID participantId) {
        log.info("Admin removing participant {} from poll {}", participantId, pollId);
        voteRepository.deleteByPollIdAndUserId(pollId, participantId);
        broadcastUpdate(pollId);

    }

    public MyVoteResponse getMyVote(Long pollId, UUID userId) {
        pollService.getPollEntity(pollId);
        List<Vote> votes = voteRepository.findByPollIdAndUserId(pollId, userId);

        List<Long> optionIds = votes.stream()
                .map(v -> v.getOption().getId())
                .toList();

        return MyVoteResponse.builder()
                .pollId(pollId)
                .optionIds(optionIds)
                .build();
    }

    public PollResultsResponse getResults(Long pollId) {
        Poll poll = pollService.getPollEntity(pollId);

        List<PollOption> options = pollOptionRepository.findByPollId(pollId);

        List<Vote> allVotes = voteRepository.findByPollId(pollId);

        List<PollResultItemResponse> resultItems = options.stream()
                .map(option -> {
                    List<UUID> optionVoterIds = allVotes.stream()
                            .filter(v -> v.getOption().getId().equals(option.getId()))
                            .map(Vote::getUserId)
                            .toList();

                    return PollResultItemResponse.builder()
                            .candidateId(option.getId())
                            .candidateName(option.getName())
                            .votes(optionVoterIds.size())
                            .voterIds(optionVoterIds)
                            .build();
                })
                .toList();

        List<UUID> participantIds = allVotes.stream()
                .map(Vote::getUserId)
                .distinct()
                .toList();

        return PollResultsResponse.builder()
                .pollId(poll.getId())
                .pollTitle(poll.getTitle())
                .totalVotes(allVotes.size())
                .results(resultItems)
                .participantIds(participantIds)
                .build();
    }

    private void broadcastUpdate(Long pollId) {
        PollResultsResponse results = getResults(pollId);
        
        messagingTemplate.convertAndSend("/topic/poll/" + pollId + "/results", results);
    }
}