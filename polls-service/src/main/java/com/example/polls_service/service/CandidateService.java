package com.example.polls_service.service;

import com.example.polls_service.dto.request.AddCandidateRequest;
import com.example.polls_service.dto.request.UpdateCandidateRequest;
import com.example.polls_service.dto.response.CandidateResponse;
import com.example.polls_service.exception.BadRequestException;
import com.example.polls_service.exception.ForbiddenException;
import com.example.polls_service.exception.NotFoundException;
import com.example.polls_service.model.Candidate;
import com.example.polls_service.model.Poll;
import com.example.polls_service.model.PollStatus;
import com.example.polls_service.repository.CandidateRepository;
import com.example.polls_service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final PollService pollService;

    public CandidateResponse addCandidate(Long pollId, AddCandidateRequest request, CurrentUser currentUser) {
        requireAdmin(currentUser);

        Poll poll = pollService.getPollEntity(pollId);
        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new BadRequestException("Cannot add candidate to a closed poll");
        }

        Candidate candidate = Candidate.builder()
                .poll(poll)
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        Candidate saved = candidateRepository.save(candidate);
        return map(saved);
    }

    public CandidateResponse updateCandidate(Long candidateId, UpdateCandidateRequest request, CurrentUser currentUser) {
        requireAdmin(currentUser);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new NotFoundException("Candidate not found with id: " + candidateId));

        Poll poll = candidate.getPoll();
        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new BadRequestException("Cannot update candidate in a closed poll");
        }

        candidate.setName(request.getName());
        candidate.setDescription(request.getDescription());

        Candidate saved = candidateRepository.save(candidate);
        return map(saved);
    }

    public void deleteCandidate(Long candidateId, CurrentUser currentUser) {
        requireAdmin(currentUser);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new NotFoundException("Candidate not found with id: " + candidateId));

        Poll poll = candidate.getPoll();
        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new BadRequestException("Cannot delete candidate from a closed poll");
        }

        candidateRepository.delete(candidate);
    }

    private void requireAdmin(CurrentUser currentUser) {
        if (!currentUser.isAdmin()) {
            throw new ForbiddenException("Admin role required");
        }
    }

    private CandidateResponse map(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .pollId(candidate.getPoll().getId())
                .name(candidate.getName())
                .description(candidate.getDescription())
                .build();
    }
}