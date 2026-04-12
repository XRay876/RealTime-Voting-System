package com.example.polls_service.service;

import com.example.polls_service.dto.request.VoteRequest;
import com.example.polls_service.dto.response.MyVoteResponse;
import com.example.polls_service.dto.response.PollResultItemResponse;
import com.example.polls_service.dto.response.PollResultsResponse;
import com.example.polls_service.exception.BadRequestException;
import com.example.polls_service.exception.ConflictException;
import com.example.polls_service.exception.NotFoundException;
import com.example.polls_service.model.Candidate;
import com.example.polls_service.model.Poll;
import com.example.polls_service.model.PollStatus;
import com.example.polls_service.model.Vote;
import com.example.polls_service.repository.CandidateRepository;
import com.example.polls_service.repository.VoteRepository;
import com.example.polls_service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final PollService pollService;
    private final SimpMessagingTemplate messagingTemplate;

    public void castVote(Long pollId, VoteRequest request, CurrentUser currentUser) {
        Poll poll = pollService.getPollEntity(pollId);

        if (poll.getStatus() != PollStatus.OPEN) {
            throw new BadRequestException("Poll is not open for voting");
        }

        Candidate candidate = candidateRepository.findByIdAndPollId(request.getCandidateId(), pollId)
                .orElseThrow(() -> new NotFoundException("Candidate not found in this poll"));

        if (voteRepository.existsByPollIdAndUserId(pollId, currentUser.getUserId())) {
            throw new ConflictException("User has already voted in this poll");
        }

        Vote vote = Vote.builder()
                .poll(poll)
                .candidate(candidate)
                .userId(currentUser.getUserId())
                .createdAt(LocalDateTime.now())
                .build();

        voteRepository.save(vote);

        PollResultsResponse results = getResults(pollId);
        messagingTemplate.convertAndSend("/topic/poll/" + pollId + "/results", results);
    }

    public MyVoteResponse getMyVote(Long pollId, CurrentUser currentUser) {
        pollService.getPollEntity(pollId);

        Vote vote = voteRepository.findByPollIdAndUserId(pollId, currentUser.getUserId())
                .orElseThrow(() -> new NotFoundException("No vote found for this user in this poll"));

        return MyVoteResponse.builder()
                .pollId(vote.getPoll().getId())
                .candidateId(vote.getCandidate().getId())
                .build();
    }

    public PollResultsResponse getResults(Long pollId) {
        Poll poll = pollService.getPollEntity(pollId);
        List<Candidate> candidates = candidateRepository.findByPollId(pollId);

        List<PollResultItemResponse> resultItems = candidates.stream()
                .map(candidate -> PollResultItemResponse.builder()
                        .candidateId(candidate.getId())
                        .candidateName(candidate.getName())
                        .votes(voteRepository.countByPollIdAndCandidateId(pollId, candidate.getId()))
                        .build())
                .toList();

        return PollResultsResponse.builder()
                .pollId(poll.getId())
                .pollTitle(poll.getTitle())
                .totalVotes(voteRepository.countByPollId(pollId))
                .results(resultItems)
                .build();
    }
}