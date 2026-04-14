package com.example.polls_service.controller;

import com.example.polls_service.dto.request.AddPollOptionRequest;
import com.example.polls_service.dto.request.UpdatePollOptionRequest;
import com.example.polls_service.dto.response.PollOptionResponse;
import com.example.polls_service.service.PollOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PollOptionController {

    private final PollOptionService pollOptionService;

    @PostMapping("/polls/{pollId}/options")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PollOptionResponse addOption(@PathVariable Long pollId,
                                        @Valid @RequestBody AddPollOptionRequest request) {
        return pollOptionService.addOption(pollId, request);
    }

    @PutMapping("/options/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PollOptionResponse updateOption(@PathVariable Long id,
                                           @Valid @RequestBody UpdatePollOptionRequest request) {
        return pollOptionService.updateOption(id, request);
    }

    @DeleteMapping("/options/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOption(@PathVariable Long id) {
        pollOptionService.deleteOption(id);
    }
}