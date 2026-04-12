package com.example.polls_service.dto.request;

import com.example.polls_service.model.PollStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePollStatusRequest {

    @NotNull
    private PollStatus status;
}
