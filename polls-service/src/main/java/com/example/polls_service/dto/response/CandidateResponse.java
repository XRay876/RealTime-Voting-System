package com.example.polls_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateResponse {
    private Long id;
    private Long pollId;
    private String name;
    private String description;
}
