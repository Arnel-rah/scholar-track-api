package dev.arnel.scholartrackapi.controller;

import dev.arnel.scholartrackapi.entity.Applicant;
import dev.arnel.scholartrackapi.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentApplicant {

    private final ApplicantService applicantService;

    public Applicant resolve(Jwt jwt) {
        String subject = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        return applicantService.getOrCreateByOidcSubject(subject, email, firstName, lastName);
    }
}