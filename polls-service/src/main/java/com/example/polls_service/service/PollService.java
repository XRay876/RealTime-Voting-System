package com.example.polls_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.polls_service.dto.request.CreatePollRequest;
import com.example.polls_service.dto.request.UpdatePollRequest;
import com.example.polls_service.dto.response.PollOptionResponse;
import com.example.polls_service.dto.response.PollResponse;
import com.example.polls_service.exception.NotFoundException;
import com.example.polls_service.model.Poll;
import com.example.polls_service.model.PollOption;
import com.example.polls_service.model.PollStatus;
import com.example.polls_service.repository.PollOptionRepository;
import com.example.polls_service.repository.PollRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;


    // create Poll
    @Transactional
    public PollResponse createPoll(CreatePollRequest request, UUID adminId) {
        log.info("Admin {} is creating a new poll: {}", adminId, request.getTitle());

        Poll poll = Poll.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(PollStatus.DRAFT)
                .multipleChoice(request.isMultipleChoice())
                .createdBy(adminId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        Poll saved = pollRepository.save(poll);
        return mapPollResponse(saved, new ArrayList<>());
    }

    public List<PollResponse> getAllPolls() {
        return pollRepository.findAll().stream()
                .map(poll -> mapPollResponse(
                        poll,
                        mapOptions(pollOptionRepository.findByPollId(poll.getId()))
                ))
                .toList();
    }

    public PollResponse getPollById(Long pollId) {
        Poll poll = getPollEntity(pollId);
        List<PollOptionResponse> options = mapOptions(pollOptionRepository.findByPollId(pollId));
        return mapPollResponse(poll, options);
    }

    public List<PollResponse> getMyPolls(UUID adminId) {
        List<Poll> polls = pollRepository.findByCreatedBy(adminId);
        return polls.stream()
                .map(poll -> mapPollResponse(
                        poll,
                        mapOptions(pollOptionRepository.findByPollId(poll.getId()))
                ))
                .toList();
    }

    // update Polls data
    @Transactional
    public PollResponse updatePoll(Long pollId, UpdatePollRequest request) {
        Poll poll = getPollEntity(pollId);
        poll.setTitle(request.getTitle());

        poll.setDescription(request.getDescription());
        poll.setMultipleChoice(request.isMultipleChoice());
        poll.setUpdatedAt(LocalDateTime.now());


        Poll saved = pollRepository.save(poll);
        return mapPollResponse(saved, mapOptions(pollOptionRepository.findByPollId(saved.getId())));
    }

    // update status of the Poll
    @Transactional
    public PollResponse updatePollStatus(Long pollId, PollStatus status) {
        Poll poll = getPollEntity(pollId);
        poll.setStatus(status);
        poll.setUpdatedAt(LocalDateTime.now());

        Poll saved = pollRepository.save(poll);
        return mapPollResponse(saved, mapOptions(pollOptionRepository.findByPollId(saved.getId())));
    }


    // deleting Polls
    @Transactional
    public void deletePoll(Long pollId) {
        Poll poll = getPollEntity(pollId);

        pollRepository.delete(poll);
    }

    public Poll getPollEntity(Long pollId) {
        
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new NotFoundException("Poll not found with id: " + pollId));
    }

    private PollResponse mapPollResponse(Poll poll, List<PollOptionResponse> options) {

        return PollResponse.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .description(poll.getDescription())
                .status(poll.getStatus())
                .multipleChoice(poll.isMultipleChoice())
                .createdBy(poll.getCreatedBy())
                .options(options)
                .build();
    }

    private List<PollOptionResponse> mapOptions(List<PollOption> options) {
        
        return options.stream()
                .map(option -> PollOptionResponse.builder()
                        .id(option.getId())
                        .pollId(option.getPoll().getId())
                        .name(option.getName())
                        .description(option.getDescription())
                        .build())
                .toList();
    }
}