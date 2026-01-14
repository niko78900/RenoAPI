package com.example.HomeReno.service;

import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.UserRole;
import com.example.HomeReno.repository.ContractorRepository;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import com.example.HomeReno.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ContractorRepository contractorRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUpSecurityContext() {
        UserPrincipal principal = new UserPrincipal("user-id", "tester", "hashed", UserRole.USER, true);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createProjectProgressAbove100Throws() {
        Project project = new Project();
        project.setProgress(101);

        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(project));
    }
}
