package dev.arnel.scholartrackapi.controller;

import dev.arnel.scholartrackapi.dto.DocumentResponse;
import dev.arnel.scholartrackapi.entity.Applicant;
import dev.arnel.scholartrackapi.entity.enums.DocumentType;
import dev.arnel.scholartrackapi.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final CurrentApplicant currentApplicant;

    @GetMapping
    public List<DocumentResponse> list(@PathVariable String applicationId, JwtAuthenticationToken auth) {
        Applicant applicant = currentApplicant.resolve(auth.getToken());
        return documentService.listForApplication(applicationId, applicant.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse upload(@PathVariable String applicationId,
                                   @RequestParam DocumentType type,
                                   @RequestParam MultipartFile file,
                                   JwtAuthenticationToken auth) {
        Applicant applicant = currentApplicant.resolve(auth.getToken());
        String s3Url = "pending-storage-integration/" + file.getOriginalFilename();
        return documentService.upload(applicationId, applicant.getId(), type, s3Url, file.getOriginalFilename());
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String applicationId,
                       @PathVariable String documentId,
                       JwtAuthenticationToken auth) {
        Applicant applicant = currentApplicant.resolve(auth.getToken());
        documentService.delete(documentId, applicationId, applicant.getId());
    }
}
