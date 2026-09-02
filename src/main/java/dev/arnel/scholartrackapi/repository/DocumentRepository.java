package dev.arnel.scholartrackapi.repository;

import dev.arnel.scholartrackapi.entity.Document;
import dev.arnel.scholartrackapi.entity.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, String> {

    List<Document> findByApplicationId(String applicationId);
    Optional<Document> findByApplicationIdAndType(String applicationId, DocumentType type);
}
