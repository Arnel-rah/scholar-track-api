package dev.arnel.scholartrackapi.service;

import dev.arnel.scholartrackapi.dto.DocumentResponse;
import dev.arnel.scholartrackapi.entity.Application;
import dev.arnel.scholartrackapi.entity.Document;
import dev.arnel.scholartrackapi.entity.enums.ApplicationStatus;
import dev.arnel.scholartrackapi.entity.enums.DocumentType;
import dev.arnel.scholartrackapi.exception.ResourceNotFoundException;
import dev.arnel.scholartrackapi.mapper.DocumentMapper;
import dev.arnel.scholartrackapi.repository.ApplicationRepository;
import dev.arnel.scholartrackapi.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;

    public List<DocumentResponse> listForApplication(String applicationId, String applicantId) {
        assertOwnership(applicationId, applicantId);
        return documentRepository.findByApplicationId(applicationId)
                .stream()
                .map(DocumentMapper::toResponse)
                .toList();
    }

    @Transactional
    public DocumentResponse upload(String applicationId, String applicantId, DocumentType type,
                                   String s3Url, String fileName) {
        Application application = applicationRepository.findByIdAndApplicantId(applicationId, applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify documents on a submitted application");
        }

        Document document = documentRepository.findByApplicationIdAndType(applicationId, type)
                .orElseGet(Document::new);

        document.setApplication(application);
        document.setType(type);
        document.setS3Url(s3Url);
        document.setFileName(fileName);

        return DocumentMapper.toResponse(documentRepository.save(document));
    }

    @Transactional
    public void delete(String documentId, String applicationId, String applicantId) {
        assertOwnership(applicationId, applicantId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        if (!document.getApplication().getId().equals(applicationId)) {
            throw new ResourceNotFoundException("Document not found: " + documentId);
        }
        if (document.getApplication().getStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("Cannot delete documents from a submitted application");
        }

        documentRepository.delete(document);
    }

    private void assertOwnership(String applicationId, String applicantId) {
        if (applicationRepository.findByIdAndApplicantId(applicationId, applicantId).isEmpty()) {
            throw new ResourceNotFoundException("Application not found: " + applicationId);
        }
    }
}
