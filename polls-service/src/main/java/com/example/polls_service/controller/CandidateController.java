package com.example.polls_service.controller;

import com.example.polls_service.dto.request.AddCandidateRequest;
import com.example.polls_service.dto.request.UpdateCandidateRequest;
import com.example.polls_service.dto.response.CandidateResponse;
import com.example.polls_service.security.CurrentUser;
import com.example.polls_service.security.RequestUserContext;
import com.example.polls_service.service.CandidateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;
    private final RequestUserContext requestUserContext;

    @PostMapping("/api/polls/{pollId}/candidates")
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateResponse addCandidate(@PathVariable Long pollId,
                                          @Valid @RequestBody AddCandidateRequest request,
                                          HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        return candidateService.addCandidate(pollId, request, currentUser);
    }

    @PutMapping("/api/candidates/{id}")
    public CandidateResponse updateCandidate(@PathVariable Long id,
                                             @Valid @RequestBody UpdateCandidateRequest request,
                                             HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        return candidateService.updateCandidate(id, request, currentUser);
    }

    @DeleteMapping("/api/candidates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCandidate(@PathVariable Long id,
                                HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        candidateService.deleteCandidate(id, currentUser);
    }
}
