package dev.arnel.scholartrackapi.dto;


import dev.arnel.scholartrackapi.entity.enums.ApplicationStatus;

import java.time.Instant;
import java.util.List;

public record ApplicationResponse(
        String id,
        ScholarshipResponse scholarship,
        ApplicationStatus status,
        Instant submittedAt,
        String notes,
        List<DocumentResponse> documents,
        boolean complete,
        Instant createdAt,
        Instant updatedAt
) {}
