package com.example.polls_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.polls_service.dto.request.CreatePollRequest;
import com.example.polls_service.dto.request.UpdatePollRequest;
import com.example.polls_service.dto.request.UpdatePollStatusRequest;
import com.example.polls_service.dto.response.PollResponse;
import com.example.polls_service.security.UserDetailsImpl;
import com.example.polls_service.service.PollService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;


    // Allows an ADMIN to create a new poll
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> createPoll(
            @Valid @RequestBody CreatePollRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        PollResponse response = pollService.createPoll(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Retrieves a list of all polls currently available in the system
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PollResponse>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    // Fetches the full details of a specific poll
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PollResponse> getPollById(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getPollById(id));
    }

    // Enables an ADMIN to modify the core details
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> updatePoll(@PathVariable Long id,
                                                   @Valid @RequestBody UpdatePollRequest request) {
        return ResponseEntity.ok(pollService.updatePoll(id, request));
    }

    // Allows an ADMIN to open or close a poll
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> updatePollStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePollStatusRequest request) {
        return ResponseEntity.ok(pollService.updatePollStatus(id, request.getStatus()));
    }

    // Completely removes a poll
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
    }


    // Returns a list of all polls created specifically by the currently ADMIN
    @GetMapping("/my-polls")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PollResponse>> getMyPolls(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        List<PollResponse> myPolls = pollService.getMyPolls(currentUser.getId());
        return ResponseEntity.ok(myPolls);
    }
}