package com.example.polls_service.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MyVoteResponse {
    private Long pollId;
    private List<Long> optionIds;
}