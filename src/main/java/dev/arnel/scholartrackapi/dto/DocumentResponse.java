package dev.arnel.scholartrackapi.dto;



import dev.arnel.scholartrackapi.entity.enums.DocumentType;

import java.time.Instant;

public record DocumentResponse(
        String id,
        DocumentType type,
        String fileName,
        Instant uploadedAt
) {}
