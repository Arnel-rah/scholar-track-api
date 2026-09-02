package dev.arnel.scholartrackapi.service;

import dev.arnel.scholartrackapi.dto.ApplicationCreateRequest;
import dev.arnel.scholartrackapi.dto.ApplicationResponse;
import dev.arnel.scholartrackapi.dto.ApplicationUpdateRequest;
import dev.arnel.scholartrackapi.entity.Applicant;
import dev.arnel.scholartrackapi.entity.Application;
import dev.arnel.scholartrackapi.entity.Scholarship;
import dev.arnel.scholartrackapi.entity.enums.ApplicationStatus;
import dev.arnel.scholartrackapi.exception.DuplicateApplicationException;
import dev.arnel.scholartrackapi.exception.ResourceNotFoundException;
import dev.arnel.scholartrackapi.mapper.ApplicationMapper;
import dev.arnel.scholartrackapi.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ScholarshipService scholarshipService;

    public List<ApplicationResponse> listMine(String applicantId) {
        return applicationRepository.findAllByApplicantIdWithDetails(applicantId)
                .stream()
                .map(ApplicationMapper::toResponse)
                .toList();
    }

    public ApplicationResponse getOwnedOrThrow(String applicationId, String applicantId) {
        return ApplicationMapper.toResponse(findOwnedEntityOrThrow(applicationId, applicantId));
    }

    private Application findOwnedEntityOrThrow(String applicationId, String applicantId) {
        return applicationRepository.findByIdAndApplicantId(applicationId, applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));
    }

    @Transactional
    public ApplicationResponse create(ApplicationCreateRequest request, Applicant applicant) {
        if (applicationRepository.existsByApplicantIdAndScholarshipId(applicant.getId(), request.scholarshipId())) {
            throw new DuplicateApplicationException(
                    "An application already exists for this scholarship");
        }

        Scholarship scholarship = scholarshipService.findEntityOrThrow(request.scholarshipId());

        Application application = new Application();
        application.setApplicant(applicant);
        application.setScholarship(scholarship);
        application.setStatus(ApplicationStatus.DRAFT);

        return ApplicationMapper.toResponse(applicationRepository.save(application));
    }

    @Transactional
    public ApplicationResponse update(String applicationId, String applicantId, ApplicationUpdateRequest request) {
        Application application = findOwnedEntityOrThrow(applicationId, applicantId);
        if (application.getStatus() != ApplicationStatus.DRAFT && request.status() == ApplicationStatus.DRAFT) {
            throw new IllegalStateException("Cannot move a submitted application back to draft");
        }

        if (request.status() != null) {
            application.setStatus(request.status());
            if (request.status() == ApplicationStatus.SUBMITTED && application.getSubmittedAt() == null) {
                application.setSubmittedAt(Instant.now());
            }
        }
        if (request.notes() != null) {
            application.setNotes(request.notes());
        }

        return ApplicationMapper.toResponse(application);
    }

    @Transactional
    public void delete(String applicationId, String applicantId) {
        Application application = findOwnedEntityOrThrow(applicationId, applicantId);
        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("Only draft applications can be deleted");
        }
        applicationRepository.delete(application);
    }

    long countByStatus(String applicantId, ApplicationStatus status) {
        return applicationRepository.countByApplicantIdAndStatus(applicantId, status);
    }
}
