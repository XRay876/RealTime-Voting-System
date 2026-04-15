package com.example.polls_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.polls_service.dto.request.AddPollOptionRequest;
import com.example.polls_service.dto.request.UpdatePollOptionRequest;
import com.example.polls_service.dto.response.PollOptionResponse;
import com.example.polls_service.service.PollOptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PollOptionController {

    private final PollOptionService pollOptionService;


    // Allows an ADMIN to add a new selectable choice
    @PostMapping("/polls/{pollId}/options")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PollOptionResponse addOption(@PathVariable Long pollId,
                                        @Valid @RequestBody AddPollOptionRequest request) {
        return pollOptionService.addOption(pollId, request);
    }

    // Enables an ADMIN to edit the text or details
    @PutMapping("/options/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PollOptionResponse updateOption(@PathVariable Long id,
                                           @Valid @RequestBody UpdatePollOptionRequest request) {
        return pollOptionService.updateOption(id, request);
    }

    // Removes a specific voting option from a poll
    @DeleteMapping("/options/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOption(@PathVariable Long id) {
        pollOptionService.deleteOption(id);
    }
}