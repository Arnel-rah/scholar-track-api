package dev.arnel.scholartrackapi.mapper;


import dev.arnel.scholartrackapi.dto.ApplicantResponse;
import dev.arnel.scholartrackapi.entity.Applicant;

public final class ApplicantMapper {

    private ApplicantMapper() {
    }

    public static ApplicantResponse toResponse(Applicant applicant) {
        return new ApplicantResponse(
                applicant.getId(),
                applicant.getEmail(),
                applicant.getFirstName(),
                applicant.getLastName()
        );
    }
}