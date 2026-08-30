package dev.arnel.scholartrackapi.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ScholarshipRequest(
        @NotBlank String name,
        @NotBlank String organization,
        String description,
        String officialUrl,
        LocalDate opensAt,
        @NotNull @FutureOrPresent LocalDate deadline
) {}
