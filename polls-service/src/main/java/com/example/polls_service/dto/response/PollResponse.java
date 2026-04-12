package com.example.polls_service.dto.response;

import com.example.polls_service.model.PollStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PollResponse {
    private Long id;
    private String title;
    private String description;
    private PollStatus status;
    private String createdBy;
    private List<CandidateResponse> candidates;
}
