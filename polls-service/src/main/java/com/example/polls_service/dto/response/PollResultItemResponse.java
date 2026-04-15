package com.example.polls_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PollResultItemResponse {
    private Long candidateId;
    private String candidateName;
    private long votes;
    private List<UUID> voterIds;
}
