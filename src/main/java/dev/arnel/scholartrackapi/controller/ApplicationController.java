package dev.arnel.scholartrackapi.controller;

import dev.arnel.scholartrackapi.dto.ApplicationCreateRequest;
import dev.arnel.scholartrackapi.dto.ApplicationResponse;
import dev.arnel.scholartrackapi.dto.ApplicationUpdateRequest;
import dev.arnel.scholartrackapi.entity.Applicant;
import dev.arnel.scholartrackapi.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CurrentApplicant currentApplicant;

    @GetMapping
    public List<ApplicationResponse> listMine(JwtAuthenticationToken auth) {
        Applicant applicant = currentApplicant.resolve(auth.getToken());
        return applicationService.listMine(applicant.getId());
    }

    @GetMapping("/{id}")
    public ApplicationResponse get(@PathVariable String id, JwtAuthenticationToken auth) {
        Applicant applicant = currentApplicant.resolve(auth.getToken());
        return applicationService.getOwnedOrThrow(id, applicant.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(@Valid @RequestBody ApplicationCreateRequest request,
                                      JwtAuthenticationToken auth) {
        Applicant applicant = currentApplicant.resolve(auth.getToken());
        return applicationService.create(request, applicant);
    }

    @PatchMapping("/{id}")
    public ApplicationResponse update(@PathVariable String id,
                                      @RequestBody ApplicationUpdateRequest request,
                                      JwtAuthenticationToken auth) {
        Applicant applicant = currentApplicant.resolve(auth.getToken());
        return applicationService.update(id, applicant.getId(), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id, JwtAuthenticationToken auth) {
        Applicant applicant = currentApplicant.resolve(auth.getToken());
        applicationService.delete(id, applicant.getId());
    }
}
