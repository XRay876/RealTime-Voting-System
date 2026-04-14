package com.example.polls_service.controller;

import com.example.polls_service.dto.request.VoteRequest;
import com.example.polls_service.dto.response.MyVoteResponse;
import com.example.polls_service.dto.response.PollResultsResponse;
import com.example.polls_service.security.UserDetailsImpl;
import com.example.polls_service.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
@Slf4j
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{id}/vote")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> castVote(
            @PathVariable Long id,
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("User {} casting vote for candidate {} in poll {}", currentUser.getId(), request.getCandidateId(), id);
        voteService.castVote(id, request.getCandidateId(), currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/my-vote")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MyVoteResponse> getMyVote(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(voteService.getMyVote(id, currentUser.getId()));
    }

    @GetMapping("/{id}/results")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PollResultsResponse> getResults(@PathVariable Long id) {
        return ResponseEntity.ok(voteService.getResults(id));
    }
}