package com.example.polls_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.polls_service.dto.request.VoteRequest;
import com.example.polls_service.dto.response.MyVoteResponse;
import com.example.polls_service.dto.response.PollResultsResponse;
import com.example.polls_service.security.UserDetailsImpl;
import com.example.polls_service.service.VoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
@Slf4j
public class VoteController {

    private final VoteService voteService;

    // Submits the current user's choice(s) for a specific poll
    @PostMapping("/{id}/vote")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> castVote(
            @PathVariable Long id,
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("User {} casting vote(s) {} in poll {}", currentUser.getId(), request.getOptionIds(), id);
        voteService.castVote(id, request, currentUser.getId());
        return ResponseEntity.ok().build();
    }


    // Allows a user to retract or cancel their previously cast vote
    @DeleteMapping("/{id}/vote")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> cancelVote(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("User {} cancelling their vote in poll {}", currentUser.getId(), id);
        voteService.cancelVote(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    // Shows the authenticated user which option(s) they have already selected in a specific poll
    @GetMapping("/{id}/my-vote")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MyVoteResponse> getMyVote(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(voteService.getMyVote(id, currentUser.getId()));
    }


    // Retrieves the aggregated voting totals and percentages for a poll in real-time
    @GetMapping("/{id}/results")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PollResultsResponse> getResults(@PathVariable Long id) {
        return ResponseEntity.ok(voteService.getResults(id));
    }

    // An ADMIN tool to remove a specific user's participation or vote from a poll
    @DeleteMapping("/{id}/participants/{participantId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParticipant(
            @PathVariable Long id,
            @PathVariable UUID participantId) {
        voteService.removeParticipantByAdmin(id, participantId);
    }
}