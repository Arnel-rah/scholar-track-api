package dev.arnel.scholartrackapi.dto;


import dev.arnel.scholartrackapi.entity.enums.ScholarshipStatus;

import java.time.LocalDate;

public record ScholarshipResponse(
        String id,
        String name,
        String organization,
        String description,
        String officialUrl,
        LocalDate opensAt,
        LocalDate deadline,
        ScholarshipStatus status
) {}
