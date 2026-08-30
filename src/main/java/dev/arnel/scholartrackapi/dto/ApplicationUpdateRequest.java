package dev.arnel.scholartrackapi.dto;


import dev.arnel.scholartrackapi.entity.enums.ApplicationStatus;

public record ApplicationUpdateRequest(
        ApplicationStatus status,
        String notes
) {}
