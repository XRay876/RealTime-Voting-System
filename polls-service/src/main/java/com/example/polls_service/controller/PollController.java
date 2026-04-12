package com.example.polls_service.controller;

import com.example.polls_service.dto.request.CreatePollRequest;
import com.example.polls_service.dto.request.UpdatePollRequest;
import com.example.polls_service.dto.request.UpdatePollStatusRequest;
import com.example.polls_service.dto.response.PollResponse;
import com.example.polls_service.security.CurrentUser;
import com.example.polls_service.security.RequestUserContext;
import com.example.polls_service.service.PollService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;
    private final RequestUserContext requestUserContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PollResponse createPoll(@Valid @RequestBody CreatePollRequest request,
                                   HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        return pollService.createPoll(request, currentUser);
    }

    @GetMapping
    public List<PollResponse> getAllPolls() {
        return pollService.getAllPolls();
    }

    @GetMapping("/{id}")
    public PollResponse getPollById(@PathVariable Long id) {
        return pollService.getPollById(id);
    }

    @PutMapping("/{id}")
    public PollResponse updatePoll(@PathVariable Long id,
                                   @Valid @RequestBody UpdatePollRequest request,
                                   HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        return pollService.updatePoll(id, request, currentUser);
    }

    @PatchMapping("/{id}/status")
    public PollResponse updateStatus(@PathVariable Long id,
                                     @Valid @RequestBody UpdatePollStatusRequest request,
                                     HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        return pollService.updatePollStatus(id, request.getStatus(), currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePoll(@PathVariable Long id,
                           HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        pollService.deletePoll(id, currentUser);
    }
}

