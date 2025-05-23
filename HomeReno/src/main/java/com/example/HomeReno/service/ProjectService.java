package com.example.HomeReno.service;


import com.example.HomeReno.entity.Project;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<Project> getAllProjects(){
        return projectRepository.findAll();
    }

    public Project createProject(Project project){
        return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(Long id){
        return projectRepository.findById(id);
    }

    public Optional<Project> getProjectByName(String name){
        return projectRepository.findByName(name);
    }

    public List<Project> getProjectsByContractorsName(String contractor){
        return projectRepository.findAllByContractor(contractor);
    }

    public Optional<Project> getProjectByAddress(String Address){
        return projectRepository.findByAddress(Address);
    }

    //Export timeline ( GET ): Export the timeline i.e
    //Task list, Estimated time of finishing, dates of each task and each milestone that has been finished/hit

    public Map<String, Object> GetTimeline(Long id){
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project NOT found"));

        Map<String, Object> response = new HashMap<>();
        response.put("ID", project.getId());
        response.put("Name", project.getName());
        response.put("Task List", project.getTaskList());
        response.put("Estimated Time to finish(Months): ", project.getETA());

        return response;
    }
}
