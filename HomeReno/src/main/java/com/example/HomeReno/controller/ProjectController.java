package com.example.HomeReno.controller;

import com.example.HomeReno.controller.dto.ProjectResponse;
import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.service.ContractorService;
import com.example.HomeReno.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ContractorService contractorService;
    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable String id) {
        return projectService.getProjectById(id)
                .map(project -> ResponseEntity.ok(toResponse(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/adr/{address}")
    public ResponseEntity<ProjectResponse> getProjectByAddress(@PathVariable String address) {
        return projectService.getProjectByAddress(address)
                .map(project -> ResponseEntity.ok(toResponse(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<ProjectResponse> getProjectByName(@PathVariable String name) {
        return projectService.getProjectByName(name)
                .map(project -> ResponseEntity.ok(toResponse(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}/finished")
    public ResponseEntity<Map<String, Boolean>> getFinishedFlag(@PathVariable String id) {
        try {
            boolean finished = projectService.getFinishedStatus(id);
            return ResponseEntity.ok(Map.of("finished", finished));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/contractor/{contractorId}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByContractor(@PathVariable String contractorId) {
        List<ProjectResponse> projects = projectService.getProjectsByContractorId(contractorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return projects.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(projects);
    }
    @GetMapping("/timeline/{id}")
    public ResponseEntity<Map<String, Object>> getTimeLine(@PathVariable String id) {
        try {
            Map<String, Object> response = projectService.getTimeline(id);
            return response.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody Project project) {
        try {
            return ResponseEntity.ok(toResponse(projectService.createProject(project)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/contractor")
    public ResponseEntity<ProjectResponse> updateProjectContractor(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        String contractorId = getString(payload, "contractorId", "contractor");
        if (contractorId == null || contractorId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Double latitude = getDouble(payload, "latitude");
        Double longitude = getDouble(payload, "longitude");
        try {
            return ResponseEntity.ok(toResponse(projectService.updateContractor(id, contractorId, latitude, longitude)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/address")
    public ResponseEntity<ProjectResponse> updateProjectAddress(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        String address = getString(payload, "address");
        if (address == null || address.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Double latitude = getDouble(payload, "latitude");
        Double longitude = getDouble(payload, "longitude");
        try {
            return ResponseEntity.ok(toResponse(projectService.updateAddress(id, address, latitude, longitude)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/name")
    public ResponseEntity<ProjectResponse> updateProjectName(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        String name = getString(payload, "name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Double latitude = getDouble(payload, "latitude");
        Double longitude = getDouble(payload, "longitude");
        try {
            return ResponseEntity.ok(toResponse(projectService.updateName(id, name, latitude, longitude)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/budget")
    public ResponseEntity<ProjectResponse> updateProjectBudget(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Object budgetValue = payload.get("budget");
        if (!(budgetValue instanceof Number number)) {
            return ResponseEntity.badRequest().build();
        }
        Double latitude = getDouble(payload, "latitude");
        Double longitude = getDouble(payload, "longitude");
        try {
            return ResponseEntity.ok(toResponse(projectService.updateBudget(id, number.doubleValue(), latitude, longitude)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/workers")
    public ResponseEntity<ProjectResponse> updateProjectWorkers(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Number workersValue = getNumber(payload, "workers", "number_of_workers", "numberOfWorkers");
        if (workersValue == null) {
            return ResponseEntity.badRequest().build();
        }
        Double latitude = getDouble(payload, "latitude");
        Double longitude = getDouble(payload, "longitude");
        try {
            return ResponseEntity.ok(toResponse(projectService.updateWorkers(id, workersValue.intValue(), latitude, longitude)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/progress")
    public ResponseEntity<ProjectResponse> updateProjectProgress(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Object progressValue = payload.get("progress");
        if (!(progressValue instanceof Number number)) {
            return ResponseEntity.badRequest().build();
        }
        Double latitude = getDouble(payload, "latitude");
        Double longitude = getDouble(payload, "longitude");
        try {
            return ResponseEntity.ok(toResponse(projectService.updateProgress(id, number.intValue(), latitude, longitude)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/eta")
    public ResponseEntity<ProjectResponse> updateProjectEta(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Number etaValue = getNumber(payload, "eta", "ETA");
        if (etaValue == null) {
            return ResponseEntity.badRequest().build();
        }
        Double latitude = getDouble(payload, "latitude");
        Double longitude = getDouble(payload, "longitude");
        try {
            return ResponseEntity.ok(toResponse(projectService.updateEta(id, etaValue.intValue(), latitude, longitude)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/finished")
    public ResponseEntity<ProjectResponse> updateFinishedFlag(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Boolean finished = getBoolean(payload, "finished");
        if (finished == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(toResponse(projectService.updateFinishedStatus(id, finished)));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/contractor/remove")
    public ResponseEntity<ProjectResponse> removeProjectContractor(@PathVariable String id) {
        try {
            return ResponseEntity.ok(toResponse(projectService.removeContractor(id)));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<ProjectResponse> addTaskToProject(
            @PathVariable String projectId,
            @RequestBody Task task
    ) {
        try {
            return ResponseEntity.ok(toResponse(projectService.addTaskToProject(projectId, task)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<Void> removeTaskFromProject(@PathVariable String projectId, @PathVariable String taskId) {
        try {
            projectService.removeTaskFromProject(projectId, taskId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    private ProjectResponse toResponse(Project project) {
        if (project == null) {
            return null;
        }
        String contractorId = project.getContractor();
        Optional<String> contractorName = Optional.empty();
        if (contractorId != null && !contractorId.isBlank()) {
            contractorName = contractorService.getContractorById(contractorId)
                    .map(c -> c.getFullName());
        }
        List<String> taskIds = project.getTaskIds();
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getAddress(),
                project.getLatitude(),
                project.getLongitude(),
                project.getBudget(),
                project.getProgress(),
                project.isFinished(),
                project.getNumber_of_workers(),
                contractorId,
                contractorName.orElse(null),
                taskIds,
                project.getETA()
        );
    }

    private static String getString(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Object value = payload.get(key);
            if (value instanceof String stringValue) {
                return stringValue;
            }
        }
        return null;
    }

    private static Double getDouble(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value instanceof Number numberValue ? numberValue.doubleValue() : null;
    }

    private static Number getNumber(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Object value = payload.get(key);
            if (value instanceof Number numberValue) {
                return numberValue;
            }
        }
        return null;
    }

    private static Boolean getBoolean(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value instanceof Boolean booleanValue ? booleanValue : null;
    }
}
