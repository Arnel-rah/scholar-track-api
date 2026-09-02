package dev.arnel.scholartrackapi.controller;

import tools.jackson.databind.ObjectMapper;
import dev.arnel.scholartrackapi.AbstractIntegrationTest;
import dev.arnel.scholartrackapi.dto.ApplicationCreateRequest;
import dev.arnel.scholartrackapi.dto.ScholarshipRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class DocumentControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createApplication(String subject) throws Exception {
        ScholarshipRequest scholarshipRequest = new ScholarshipRequest(
                "Doc Test Scholarship", "Org", null, null,
                LocalDate.now(), LocalDate.now().plusMonths(1));

        String scholarshipResponse = mockMvc.perform(post("/api/scholarships")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scholarshipRequest)))
                .andReturn().getResponse().getContentAsString();
        String scholarshipId = objectMapper.readTree(scholarshipResponse).get("id").asText();

        ApplicationCreateRequest applicationRequest = new ApplicationCreateRequest(scholarshipId);
        String applicationResponse = mockMvc.perform(post("/api/applications")
                        .with(jwt().jwt(j -> j.claim("sub", subject)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequest)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(applicationResponse).get("id").asText();
    }

    @Test
    void uploadingTwiceWithSameType_replacesRatherThanDuplicates() throws Exception {
        String applicationId = createApplication("doc-user-1");

        MockMultipartFile firstCv = new MockMultipartFile(
                "file", "cv_v1.pdf", MediaType.APPLICATION_PDF_VALUE, "content-v1".getBytes());

        mockMvc.perform(multipart("/api/applications/" + applicationId + "/documents")
                        .file(firstCv)
                        .param("type", "CV")
                        .with(jwt().jwt(j -> j.claim("sub", "doc-user-1"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("cv_v1.pdf"));

        MockMultipartFile secondCv = new MockMultipartFile(
                "file", "cv_v2.pdf", MediaType.APPLICATION_PDF_VALUE, "content-v2".getBytes());

        mockMvc.perform(multipart("/api/applications/" + applicationId + "/documents")
                        .file(secondCv)
                        .param("type", "CV")
                        .with(jwt().jwt(j -> j.claim("sub", "doc-user-1"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("cv_v2.pdf"));
        mockMvc.perform(get("/api/applications/" + applicationId + "/documents")
                        .with(jwt().jwt(j -> j.claim("sub", "doc-user-1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fileName").value("cv_v2.pdf"));
    }

    @Test
    void documentUpload_isRejectedForNonOwner() throws Exception {
        String applicationId = createApplication("real-owner");

        MockMultipartFile cv = new MockMultipartFile(
                "file", "cv.pdf", MediaType.APPLICATION_PDF_VALUE, "content".getBytes());

        mockMvc.perform(multipart("/api/applications/" + applicationId + "/documents")
                        .file(cv)
                        .param("type", "CV")
                        .with(jwt().jwt(j -> j.claim("sub", "intruder"))))
                .andExpect(status().isNotFound());
    }
}
