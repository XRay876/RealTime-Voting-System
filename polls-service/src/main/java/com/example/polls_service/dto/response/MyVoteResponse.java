package com.example.polls_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyVoteResponse {
    private Long pollId;
    private Long candidateId;
}
