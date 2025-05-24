package com.example.HomeReno.service;


import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
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

    public void deleteProject(Long id){
        projectRepository.deleteById(id);
    }

    public Project addTaskToProject(Long ProjectId, Task task){
        return projectRepository.findById(ProjectId).map(project -> {
            project.getTaskList().add(task);
            task.setProject(project);
            taskRepository.save(task);
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public void removeTaskFromProject(Long ProjectId, Long TaskId){
        Project project = projectRepository.findById(ProjectId)
                .orElseThrow(() -> new RuntimeException("Project was not found"));

        project.getTaskList().removeIf(task -> task.getId() == TaskId);

        projectRepository.save(project);
    }

    public Project ChangeContractorOnProject(Long Id, String Contractor){
        Project project = projectRepository.findById(Id).orElseThrow(() -> new RuntimeException("Project was not found"));

        project.setContractor(Contractor);

        return projectRepository.save(project);
    }

    public Project ChangeAddressOnProject(Long Id, String Address){
        Project project = projectRepository.findById(Id).orElseThrow(() -> new RuntimeException("Project was not found!"));

        project.setAddress(Address);

        return projectRepository.save(project);
    }

    public Project ChangeBudgetOnProject(Long Id, Double Budget){
        Project project = projectRepository.findById(Id).orElseThrow(() -> new RuntimeException("Project was not found!"));

        project.setBudget(Budget);
        double workerratio = (Budget / 2) / 1500;
        project.setNumber_of_workers((int) workerratio);

        return projectRepository.save(project);
    }


}
