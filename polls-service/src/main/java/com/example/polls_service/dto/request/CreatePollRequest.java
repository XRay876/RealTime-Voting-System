package com.example.polls_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePollRequest {

    @NotBlank
    private String title;

    private String description;
}
