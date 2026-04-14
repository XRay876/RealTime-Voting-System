package com.example.polls_service.controller;

import com.example.polls_service.dto.request.CreatePollRequest;
import com.example.polls_service.dto.request.UpdatePollRequest;
import com.example.polls_service.dto.request.UpdatePollStatusRequest;
import com.example.polls_service.dto.response.PollResponse;
import com.example.polls_service.security.UserDetailsImpl;
import com.example.polls_service.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> createPoll(
            @Valid @RequestBody CreatePollRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        PollResponse response = pollService.createPoll(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PollResponse>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PollResponse> getPollById(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getPollById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> updatePoll(@PathVariable Long id,
                                                   @Valid @RequestBody UpdatePollRequest request) {
        return ResponseEntity.ok(pollService.updatePoll(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> updatePollStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePollStatusRequest request) {
        return ResponseEntity.ok(pollService.updatePollStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
    }

    @GetMapping("/my-polls")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PollResponse>> getMyPolls(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        List<PollResponse> myPolls = pollService.getMyPolls(currentUser.getId());
        return ResponseEntity.ok(myPolls);
    }
}