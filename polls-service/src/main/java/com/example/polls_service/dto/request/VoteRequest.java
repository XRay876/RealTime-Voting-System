package com.example.polls_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class VoteRequest {
    @NotEmpty(message = "You must select at least one option")
    private List<Long> optionIds;
}