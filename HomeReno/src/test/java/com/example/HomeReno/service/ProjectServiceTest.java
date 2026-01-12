package com.example.HomeReno.service;

import com.example.HomeReno.entity.Project;
import com.example.HomeReno.repository.ContractorRepository;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void createProjectProgressAbove100Throws() {
        Project project = new Project();
        project.setProgress(101);

        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(project));
    }
}
