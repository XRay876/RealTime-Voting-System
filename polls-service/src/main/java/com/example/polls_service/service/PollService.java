package com.example.polls_service.service;

import com.example.polls_service.dto.request.CreatePollRequest;
import com.example.polls_service.dto.request.UpdatePollRequest;
import com.example.polls_service.dto.response.CandidateResponse;
import com.example.polls_service.dto.response.PollResponse;
import com.example.polls_service.exception.NotFoundException;
import com.example.polls_service.model.Candidate;
import com.example.polls_service.model.Poll;
import com.example.polls_service.model.PollStatus;
import com.example.polls_service.repository.CandidateRepository;
import com.example.polls_service.repository.PollRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final CandidateRepository candidateRepository;

    @Transactional
    public PollResponse createPoll(CreatePollRequest request, UUID adminId) {
        log.info("Admin {} is creating a new poll: {}", adminId, request.getTitle());
        Poll poll = new Poll();
        poll.setTitle(request.getTitle());
        poll.setDescription(request.getDescription());
        poll.setStatus(PollStatus.DRAFT);
        poll.setCreatedBy(adminId);
        poll.setCreatedAt(LocalDateTime.now());
        poll.setUpdatedAt(LocalDateTime.now());

        Poll saved = pollRepository.save(poll);
        return mapPollResponse(saved, new ArrayList<>());
    }

    public List<PollResponse> getAllPolls() {
        return pollRepository.findAll().stream()
                .map(poll -> mapPollResponse(
                        poll,
                        mapCandidates(candidateRepository.findByPollId(poll.getId()))
                ))
                .toList();
    }

    public PollResponse getPollById(Long pollId) {
        Poll poll = getPollEntity(pollId);
        List<CandidateResponse> candidates = mapCandidates(candidateRepository.findByPollId(pollId));
        return mapPollResponse(poll, candidates);
    }

    public List<PollResponse> getMyPolls(UUID adminId) {
        log.info("Fetching polls created by admin: {}", adminId);

        List<Poll> polls = pollRepository.findByCreatedBy(adminId);

        return polls.stream()
                .map(poll -> mapPollResponse(
                        poll,
                        mapCandidates(candidateRepository.findByPollId(poll.getId()))
                ))
                .toList();
    }

    @Transactional
    public PollResponse updatePoll(Long pollId, UpdatePollRequest request) {
        Poll poll = getPollEntity(pollId);
        poll.setTitle(request.getTitle());
        poll.setDescription(request.getDescription());
        poll.setUpdatedAt(LocalDateTime.now());

        Poll saved = pollRepository.save(poll);
        List<CandidateResponse> candidates = mapCandidates(candidateRepository.findByPollId(saved.getId()));
        return mapPollResponse(saved, candidates);
    }

    @Transactional
    public PollResponse updatePollStatus(Long pollId, PollStatus status) {
        Poll poll = getPollEntity(pollId);
        poll.setStatus(status);
        poll.setUpdatedAt(LocalDateTime.now());

        Poll saved = pollRepository.save(poll);
        List<CandidateResponse> candidates = mapCandidates(candidateRepository.findByPollId(saved.getId()));
        return mapPollResponse(saved, candidates);
    }

    @Transactional
    public void deletePoll(Long pollId) {
        Poll poll = getPollEntity(pollId);
        pollRepository.delete(poll);
    }

    public Poll getPollEntity(Long pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new NotFoundException("Poll not found with id: " + pollId));
    }

    private PollResponse mapPollResponse(Poll poll, List<CandidateResponse> candidates) {
        return PollResponse.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .description(poll.getDescription())
                .status(poll.getStatus())
                .createdBy(poll.getCreatedBy())
                .candidates(candidates)
                .build();
    }

    private List<CandidateResponse> mapCandidates(List<Candidate> candidates) {
        return candidates.stream()
                .map(candidate -> CandidateResponse.builder()
                        .id(candidate.getId())
                        .pollId(candidate.getPoll().getId())
                        .name(candidate.getName())
                        .description(candidate.getDescription())
                        .build())
                .toList();
    }
}