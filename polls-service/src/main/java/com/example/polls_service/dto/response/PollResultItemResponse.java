package com.example.polls_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PollResultItemResponse {
    private Long candidateId;
    private String candidateName;
    private long votes;
}
