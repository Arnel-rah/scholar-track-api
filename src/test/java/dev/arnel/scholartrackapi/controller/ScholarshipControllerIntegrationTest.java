package dev.arnel.scholartrackapi.controller;


import dev.arnel.scholartrackapi.AbstractIntegrationTest;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import dev.arnel.scholartrackapi.dto.ScholarshipRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ScholarshipControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndListScholarship_roundTrips() throws Exception {
        ScholarshipRequest request = new ScholarshipRequest(
                "Mastercard Foundation Scholars Program",
                "Mastercard Foundation",
                "Full scholarship for African students",
                "https://mastercardfdn.org",
                LocalDate.now(),
                LocalDate.now().plusMonths(3)
        );

        mockMvc.perform(post("/api/scholarships")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mastercard Foundation Scholars Program"))
                .andExpect(jsonPath("$.status").value("OPEN"));

        mockMvc.perform(get("/api/scholarships").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].organization").value("Mastercard Foundation"));
    }

    @Test
    void createScholarship_rejectsInvalidPayload() throws Exception {
        String invalidJson = """
                {"organization": "Some Org", "deadline": "2020-01-01"}
                """;

        mockMvc.perform(post("/api/scholarships")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listScholarships_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/scholarships"))
                .andExpect(status().isUnauthorized());
    }
}