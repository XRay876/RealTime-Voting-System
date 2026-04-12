package com.example.polls_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCandidateRequest {

    @NotBlank
    private String name;

    private String description;
}
