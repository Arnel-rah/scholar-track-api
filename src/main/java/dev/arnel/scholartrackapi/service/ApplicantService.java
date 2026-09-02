package dev.arnel.scholartrackapi.service;

import dev.arnel.scholartrackapi.entity.Applicant;
import dev.arnel.scholartrackapi.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicantService {

    private final ApplicantRepository applicantRepository;

    @Transactional
    public Applicant getOrCreateByOidcSubject(String oidcSubject, String email, String firstName, String lastName) {
        return applicantRepository.findByOidcSubject(oidcSubject)
                .orElseGet(() -> {
                    Applicant applicant = new Applicant();
                    applicant.setOidcSubject(oidcSubject);
                    applicant.setEmail(email);
                    applicant.setFirstName(firstName);
                    applicant.setLastName(lastName);
                    return applicantRepository.save(applicant);
                });
    }

    public Applicant getByIdOrThrow(String id) {
        return applicantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Applicant not found: " + id));
    }
}