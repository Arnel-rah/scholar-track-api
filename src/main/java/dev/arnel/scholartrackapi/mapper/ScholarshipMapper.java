package dev.arnel.scholartrackapi.mapper;


import dev.arnel.scholartrackapi.dto.ScholarshipRequest;
import dev.arnel.scholartrackapi.dto.ScholarshipResponse;
import dev.arnel.scholartrackapi.entity.Scholarship;

public final class ScholarshipMapper {

    private ScholarshipMapper() {
    }

    public static ScholarshipResponse toResponse(Scholarship scholarship) {
        return new ScholarshipResponse(
                scholarship.getId(),
                scholarship.getName(),
                scholarship.getOrganization(),
                scholarship.getDescription(),
                scholarship.getOfficialUrl(),
                scholarship.getOpensAt(),
                scholarship.getDeadline(),
                scholarship.getStatus()
        );
    }

    public static Scholarship toEntity(ScholarshipRequest request) {
        Scholarship scholarship = new Scholarship();
        scholarship.setName(request.name());
        scholarship.setOrganization(request.organization());
        scholarship.setDescription(request.description());
        scholarship.setOfficialUrl(request.officialUrl());
        scholarship.setOpensAt(request.opensAt());
        scholarship.setDeadline(request.deadline());
        return scholarship;
    }
}