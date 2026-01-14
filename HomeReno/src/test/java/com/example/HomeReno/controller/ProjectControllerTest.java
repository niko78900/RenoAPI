package com.example.HomeReno.controller;

import com.example.HomeReno.service.ContractorService;
import com.example.HomeReno.service.ProjectService;
import com.example.HomeReno.security.CustomUserDetailsService;
import com.example.HomeReno.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@Import(ApiKeyFilter.class)
@TestPropertySource(properties = "api.key=test-key")
class ProjectControllerTest {
    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String API_KEY = "test-key";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ContractorService contractorService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void apiKeyMissingReturns401() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of());

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteMissingProjectReturns404() throws Exception {
        doThrow(new RuntimeException("Project not found"))
                .when(projectService)
                .deleteProject("missing");

        mockMvc.perform(delete("/api/projects/{id}", "missing")
                        .header(API_KEY_HEADER, API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTaskWrongProjectReturns400() throws Exception {
        doThrow(new IllegalArgumentException("Task does not belong to project"))
                .when(projectService)
                .removeTaskFromProject("project-id", "task-id");

        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{taskId}", "project-id", "task-id")
                        .header(API_KEY_HEADER, API_KEY))
                .andExpect(status().isBadRequest());
    }
}
