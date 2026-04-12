package com.example.polls_service.controller;

import com.example.polls_service.dto.request.VoteRequest;
import com.example.polls_service.dto.response.MyVoteResponse;
import com.example.polls_service.dto.response.PollResultsResponse;
import com.example.polls_service.security.CurrentUser;
import com.example.polls_service.security.RequestUserContext;
import com.example.polls_service.service.VoteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final RequestUserContext requestUserContext;

    @PostMapping("/{id}/vote")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> castVote(@PathVariable Long id,
                                        @Valid @RequestBody VoteRequest request,
                                        HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        voteService.castVote(id, request, currentUser);
        return Map.of("message", "Vote recorded successfully");
    }

    @GetMapping("/{id}/my-vote")
    public MyVoteResponse getMyVote(@PathVariable Long id,
                                    HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = requestUserContext.getCurrentUser(httpServletRequest);
        return voteService.getMyVote(id, currentUser);
    }

    @GetMapping("/{id}/results")
    public PollResultsResponse getResults(@PathVariable Long id) {
        return voteService.getResults(id);
    }
}