package dev.arnel.scholartrackapi.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplicationCreateRequest(
        @NotBlank String scholarshipId
) {}
