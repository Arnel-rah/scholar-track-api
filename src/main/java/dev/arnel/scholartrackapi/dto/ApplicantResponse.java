package dev.arnel.scholartrackapi.dto;

public record ApplicantResponse(
        String id,
        String email,
        String firstName,
        String lastName
) {}