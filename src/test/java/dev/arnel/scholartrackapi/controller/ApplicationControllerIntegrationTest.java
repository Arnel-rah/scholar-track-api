package dev.arnel.scholartrackapi.controller;

import tools.jackson.databind.json.JsonMapper;
import dev.arnel.scholartrackapi.AbstractIntegrationTest;
import dev.arnel.scholartrackapi.dto.ApplicationCreateRequest;
import dev.arnel.scholartrackapi.dto.ApplicationUpdateRequest;
import dev.arnel.scholartrackapi.dto.ScholarshipRequest;
import dev.arnel.scholartrackapi.entity.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApplicationControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    private String createScholarship() throws Exception {
        ScholarshipRequest request = new ScholarshipRequest(
                "Test Scholarship", "Test Org", null, null,
                LocalDate.now(), LocalDate.now().plusMonths(1));

        String response = mockMvc.perform(post("/api/scholarships")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void createApplication_thenDuplicateIsRejected() throws Exception {
        String scholarshipId = createScholarship();
        ApplicationCreateRequest createRequest = new ApplicationCreateRequest(scholarshipId);

        mockMvc.perform(post("/api/applications")
                        .with(jwt().jwt(j -> j.claim("sub", "user-1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        mockMvc.perform(post("/api/applications")
                        .with(jwt().jwt(j -> j.claim("sub", "user-1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void applicationIsNotVisibleToADifferentApplicant() throws Exception {
        String scholarshipId = createScholarship();
        ApplicationCreateRequest createRequest = new ApplicationCreateRequest(scholarshipId);

        String response = mockMvc.perform(post("/api/applications")
                        .with(jwt().jwt(j -> j.claim("sub", "owner")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        String applicationId = objectMapper.readTree(response).get("id").asText();
        mockMvc.perform(get("/api/applications/" + applicationId)
                        .with(jwt().jwt(j -> j.claim("sub", "someone-else"))))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/applications/" + applicationId)
                        .with(jwt().jwt(j -> j.claim("sub", "owner"))))
                .andExpect(status().isOk());
    }

    @Test
    void submittedApplication_cannotRevertToDraft() throws Exception {
        String scholarshipId = createScholarship();
        ApplicationCreateRequest createRequest = new ApplicationCreateRequest(scholarshipId);

        String response = mockMvc.perform(post("/api/applications")
                        .with(jwt().jwt(j -> j.claim("sub", "user-2")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        String applicationId = objectMapper.readTree(response).get("id").asText();

        ApplicationUpdateRequest submit = new ApplicationUpdateRequest(ApplicationStatus.SUBMITTED, null);
        mockMvc.perform(patch("/api/applications/" + applicationId)
                        .with(jwt().jwt(j -> j.claim("sub", "user-2")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.submittedAt").exists());

        ApplicationUpdateRequest revert = new ApplicationUpdateRequest(ApplicationStatus.DRAFT, null);
        mockMvc.perform(patch("/api/applications/" + applicationId)
                        .with(jwt().jwt(j -> j.claim("sub", "user-2")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revert)))
                .andExpect(status().isConflict());
    }

    @Test
    void onlyDraftApplications_canBeDeleted() throws Exception {
        String scholarshipId = createScholarship();
        ApplicationCreateRequest createRequest = new ApplicationCreateRequest(scholarshipId);

        String response = mockMvc.perform(post("/api/applications")
                        .with(jwt().jwt(j -> j.claim("sub", "user-3")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        String applicationId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/api/applications/" + applicationId)
                        .with(jwt().jwt(j -> j.claim("sub", "user-3"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/applications/" + applicationId)
                        .with(jwt().jwt(j -> j.claim("sub", "user-3"))))
                .andExpect(status().isNotFound());
    }
}