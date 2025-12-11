package com.example.HomeReno.controller;

import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.service.ProjectService;
import com.example.HomeReno.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    // -------------------------
    // GET ALL PROJECTS
    // -------------------------
    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    // -------------------------
    // GET PROJECT BY ID
    // -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------
    // GET PROJECT BY ADDRESS
    // -------------------------
    @GetMapping("/adr/{address}")
    public ResponseEntity<Project> getProjectByAddress(@PathVariable String address) {
        return projectService.getProjectByAddress(address)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------
    // GET PROJECT BY NAME
    // -------------------------
    @GetMapping("/name/{name}")
    public ResponseEntity<Project> getProjectByName(@PathVariable String name) {
        return projectService.getProjectByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------
    // GET BY CONTRACTOR NAME
    // -------------------------
    @GetMapping("/cname/{contractor}")
    public ResponseEntity<List<Project>> getProjectsByContractorName(@PathVariable String contractor) {
        List<Project> projects = projectService.getProjectsByContractorsName(contractor);
        return projects.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(projects);
    }

    // -------------------------
    // TIMELINE
    // -------------------------
    @GetMapping("/timeline/{id}")
    public ResponseEntity<Map<String, Object>> getTimeLine(@PathVariable String id) {
        Map<String, Object> response = projectService.GetTimeline(id);
        return response.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

    // -------------------------
    // CREATE PROJECT
    // -------------------------
    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    // -------------------------
    // DELETE PROJECT
    // -------------------------
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
    }

    // -------------------------
    // ADD TASK TO PROJECT
    // -------------------------
    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<Project> addTaskToProject(
            @PathVariable String projectId,
            @RequestBody Task task
    ) {
        try {
            return ResponseEntity.ok(projectService.addTaskToProject(projectId, task));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------
    // REMOVE TASK FROM PROJECT
    // -------------------------
    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public void removeTaskFromProject(@PathVariable String projectId, @PathVariable String taskId) {
        projectService.removeTaskFromProject(projectId, taskId);
    }
}
