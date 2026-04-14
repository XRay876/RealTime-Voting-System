package com.example.polls_service.controller;

import com.example.polls_service.dto.request.AddCandidateRequest;
import com.example.polls_service.dto.request.UpdateCandidateRequest;
import com.example.polls_service.dto.response.CandidateResponse;
import com.example.polls_service.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping("/polls/{pollId}/candidates")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CandidateResponse addCandidate(@PathVariable Long pollId,
                                          @Valid @RequestBody AddCandidateRequest request) {
        return candidateService.addCandidate(pollId, request);
    }

    @PutMapping("/candidates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CandidateResponse updateCandidate(@PathVariable Long id,
                                             @Valid @RequestBody UpdateCandidateRequest request) {
        return candidateService.updateCandidate(id, request);
    }

    @DeleteMapping("/candidates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
    }
}