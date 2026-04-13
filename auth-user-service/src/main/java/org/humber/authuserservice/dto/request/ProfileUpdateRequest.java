package org.humber.authuserservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "First name is required") @Size(min = 2, max = 50) String firstName,
        @NotBlank(message = "Last name is required") @Size(min = 2, max = 50) String lastName
) {}