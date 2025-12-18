package com.example.HomeReno.service;


import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Optional<Project> getProjectById(String id){
        return projectRepository.findById(id);
    }

    public Optional<Project> getProjectByName(String name){
        return projectRepository.findByName(name);
    }

    public List<Project> getProjectsByContractorId(String contractorId){
        return projectRepository.findByContractorId(contractorId);
    }

    public Optional<Project> getProjectByAddress(String address){
        return projectRepository.findByAddress(address);
    }

    public Map<String, Object> getTimeline(String id){
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project NOT found"));

        Map<String, Object> response = new HashMap<>();
        response.put("ID", project.getId());
        response.put("Name", project.getName());
        response.put("Progress", project.getProgress() + " %");
        response.put("Task List", project.getTaskIds());
        response.put("Estimated Time to finish: ", project.getETA() + " Months");

        return response;
    }

    public void deleteProject(String id){
        projectRepository.deleteById(id);
    }

    public Project addTaskToProject(String projectId, Task task) {
        return projectRepository.findById(projectId).map(project -> {
            task.setProjectId(projectId);
            Task savedTask = taskRepository.save(task);
            List<String> taskIds = project.getTaskIds();
            if (taskIds == null) {
                taskIds = new ArrayList<>();
                project.setTaskIds(taskIds);
            }
            taskIds.add(savedTask.getId());
            return projectRepository.save(project);

        }).orElseThrow(() -> new RuntimeException("Project not found"));
    }


    public void removeTaskFromProject(String projectId, String taskId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project was not found"));

        List<String> taskIds = project.getTaskIds();
        if (taskIds != null) {
            taskIds.removeIf(id -> id.equals(taskId));
        }

        projectRepository.save(project);
        taskRepository.deleteById(taskId);
    }

    public Project updateContractor(String id, String contractorId){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found"));

        project.setContractor(contractorId);

        return projectRepository.save(project);
    }

    public Project updateAddress(String id, String address){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));

        project.setAddress(address);

        return projectRepository.save(project);
    }

    public Project updateName(String id, String name){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));

        project.setName(name);

        return projectRepository.save(project);
    }

    public Project updateBudget(String id, Double budget){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));

        project.setBudget(budget);
        double workerratio = (budget / 2) / 1500;
        project.setNumber_of_workers((int) workerratio);

        return projectRepository.save(project);
    }

    public Project updateProgress(String id, Integer progress){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));
        project.setProgress(progress);
        return projectRepository.save(project);
    }

    public Project updateEta(String id, Integer eta){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));
        project.setETA(eta);
        return projectRepository.save(project);
    }

    public Project removeContractor(String id){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));
        project.setContractor(null);
        return projectRepository.save(project);
    }


}
