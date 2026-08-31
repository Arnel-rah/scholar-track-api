package dev.arnel.scholartrackapi.mapper;


import dev.arnel.scholartrackapi.dto.DocumentResponse;
import dev.arnel.scholartrackapi.entity.Document;

public final class DocumentMapper {

    private DocumentMapper() {
    }

    public static DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getType(),
                document.getFileName(),
                document.getUploadedAt()
        );
    }
}
