package dev.arnel.scholartrackapi.mapper;


import dev.arnel.scholartrackapi.dto.ApplicationResponse;
import dev.arnel.scholartrackapi.entity.Application;
import dev.arnel.scholartrackapi.entity.enums.DocumentType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ApplicationMapper {

    private static final Set<DocumentType> REQUIRED_DOCUMENT_TYPES =
            Set.copyOf(Arrays.asList(DocumentType.values()));

    private ApplicationMapper() {
    }

    public static ApplicationResponse toResponse(Application application) {
        List<com.scholartrack.api.dto.DocumentResponse> documents = application.getDocuments()
                .stream()
                .map(DocumentMapper::toResponse)
                .collect(Collectors.toList());

        boolean complete = isComplete(application);

        return new ApplicationResponse(
                application.getId(),
                ScholarshipMapper.toResponse(application.getScholarship()),
                application.getStatus(),
                application.getSubmittedAt(),
                application.getNotes(),
                documents,
                complete,
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    private static boolean isComplete(Application application) {
        Set<DocumentType> uploadedTypes = application.getDocuments().stream()
                .map(com.scholartrack.api.entity.Document::getType)
                .collect(Collectors.toSet());
        return uploadedTypes.containsAll(REQUIRED_DOCUMENT_TYPES);
    }
}
