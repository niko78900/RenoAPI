package com.example.HomeReno.controller;


import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import com.example.HomeReno.service.ProjectService;
import com.example.HomeReno.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class RenovationsController {

    @Autowired
    ProjectService ProjectService;

    @Autowired
    TaskService TaskService;

    @Autowired
    ProjectRepository ProjectRepository;

    @Autowired
    TaskRepository TaskRepository;


    @GetMapping
    public List<Project> getAllProjects(){
        return ProjectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id){
        Optional<Project> project = ProjectService.getProjectById(id);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/adr/{address}")
    public ResponseEntity<Project> getProjectByAddress(@PathVariable String address){
        Optional<Project> project = ProjectService.getProjectByAddress(address);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Project> getProjectByName(@PathVariable String name){
        Optional<Project> project = ProjectService.getProjectByName(name);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cname/{contractor}")
    public ResponseEntity<List<Project>> getProjectsByContractorName(@PathVariable String contractor){
        List<Project> projects = ProjectService.getProjectsByContractorsName(contractor);
        if (projects.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(projects); // 200 OK with the list
    }
    //Export timeline ( GET ): Export the timeline i.e
    //Task list, Estimated time of finishing, dates of each task and each milestone that has been finished/hit

    @GetMapping("/timeline/{id}")
    public ResponseEntity<Map<String, Object>> getTimeLine(@PathVariable Long id){
        Map<String, Object> response = ProjectService.GetTimeline(id);
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addProject")
    public Project createProject(@RequestBody Project project){
        return ProjectService.createProject(project);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id){
        ProjectService.deleteProject(id);
    }

    @PostMapping("/{ProjectId}/tasks")
    public ResponseEntity<Project> addTaskToProject(@PathVariable long ProjectId, @RequestBody Task task){
        try{
            return ResponseEntity.ok(ProjectService.addTaskToProject(ProjectId, task));
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{ProjectId}/tasks/{taskId}")
    public void removeTaskFromproject(@PathVariable long ProjectId, @PathVariable Long taskId){
        try {
            ProjectService.removeTaskFromProject(ProjectId, taskId);
        } catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    @PatchMapping("/{ProjectId}/cname")
    public Project ChangeContractorInProject(@PathVariable long ProjectId, @RequestBody String Contractor){
        return ProjectService.ChangeContractorOnProject(ProjectId, Contractor);
    }

    @PatchMapping("/{ProjectId}/Address")
    public Project ChangeAddressInProject(@PathVariable long ProjectId, @RequestBody String Address){
        return ProjectService.ChangeAddressOnProject(ProjectId, Address);
    }

    @PatchMapping("/{ProjectId}/Budget")
    public Project ChangeBudgetInProject(@PathVariable long ProjectId, @RequestBody Double Budget){
        return ProjectService.ChangeBudgetOnProject(ProjectId, Budget);
    }

}
