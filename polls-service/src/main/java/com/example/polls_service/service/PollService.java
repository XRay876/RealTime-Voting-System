package com.example.polls_service.service;

import com.example.polls_service.dto.request.CreatePollRequest;
import com.example.polls_service.dto.request.UpdatePollRequest;
import com.example.polls_service.dto.response.CandidateResponse;
import com.example.polls_service.dto.response.PollResponse;
import com.example.polls_service.exception.ForbiddenException;
import com.example.polls_service.exception.NotFoundException;
import com.example.polls_service.model.Candidate;
import com.example.polls_service.model.Poll;
import com.example.polls_service.model.PollStatus;
import com.example.polls_service.repository.CandidateRepository;
import com.example.polls_service.repository.PollRepository;
import com.example.polls_service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final CandidateRepository candidateRepository;

    public PollResponse createPoll(CreatePollRequest request, CurrentUser currentUser) {
        requireAdmin(currentUser);

        Poll poll = Poll.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(PollStatus.DRAFT)
                .createdBy(currentUser.getUserId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Poll saved = pollRepository.save(poll);
        return mapPollResponse(saved, List.of());
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

    public PollResponse updatePoll(Long pollId, UpdatePollRequest request, CurrentUser currentUser) {
        requireAdmin(currentUser);

        Poll poll = getPollEntity(pollId);
        poll.setTitle(request.getTitle());
        poll.setDescription(request.getDescription());
        poll.setUpdatedAt(LocalDateTime.now());

        Poll saved = pollRepository.save(poll);
        List<CandidateResponse> candidates = mapCandidates(candidateRepository.findByPollId(saved.getId()));
        return mapPollResponse(saved, candidates);
    }

    public PollResponse updatePollStatus(Long pollId, PollStatus status, CurrentUser currentUser) {
        requireAdmin(currentUser);

        Poll poll = getPollEntity(pollId);
        poll.setStatus(status);
        poll.setUpdatedAt(LocalDateTime.now());

        Poll saved = pollRepository.save(poll);
        List<CandidateResponse> candidates = mapCandidates(candidateRepository.findByPollId(saved.getId()));
        return mapPollResponse(saved, candidates);
    }

    public void deletePoll(Long pollId, CurrentUser currentUser) {
        requireAdmin(currentUser);

        Poll poll = getPollEntity(pollId);
        List<Candidate> candidates = candidateRepository.findByPollId(pollId);
        candidateRepository.deleteAll(candidates);
        pollRepository.delete(poll);
    }

    public Poll getPollEntity(Long pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new NotFoundException("Poll not found with id: " + pollId));
    }

    private void requireAdmin(CurrentUser currentUser) {
        if (!currentUser.isAdmin()) {
            throw new ForbiddenException("Admin role required");
        }
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
                        .pollId(candidate.getPollId())
                        .name(candidate.getName())
                        .description(candidate.getDescription())
                        .build())
                .toList();
    }
}
