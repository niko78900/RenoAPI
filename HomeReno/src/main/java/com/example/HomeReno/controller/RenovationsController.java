package com.example.HomeReno.controller;


import com.example.HomeReno.entity.Project;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import com.example.HomeReno.service.ProjectService;
import com.example.HomeReno.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
