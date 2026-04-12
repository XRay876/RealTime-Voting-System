package com.example.polls_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PollResultsResponse {
    private Long pollId;
    private String pollTitle;
    private long totalVotes;
    private List<PollResultItemResponse> results;
}
