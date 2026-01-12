package com.example.HomeReno.service;


import com.example.HomeReno.entity.Image;
import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.ContractorRepository;
import com.example.HomeReno.repository.ImageRepository;
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

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private ImageRepository imageRepository;

    public List<Project> getAllProjects(){
        return projectRepository.findAll();
    }

    public Project createProject(Project project){
        String contractorId = project.getContractor();
        if (contractorId != null && !contractorId.isBlank()) {
            contractorRepository.findById(contractorId)
                    .orElseThrow(() -> new RuntimeException("Contractor not found"));
        }
        validateBudget(project.getBudget());
        validateProgress(project.getProgress());
        validateWorkers(project.getNumber_of_workers());
        validateEta(project.getETA());
        validateCoordinates(project.getLatitude(), project.getLongitude());
        project.setTaskIds(new ArrayList<>());
        project.setImageIds(new ArrayList<>());
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

    public boolean getFinishedStatus(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project NOT found"));
        return project.isFinished();
    }

    public void deleteProject(String id){
        projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<Task> tasks = taskRepository.findByProjectId(id);
        if (!tasks.isEmpty()) {
            taskRepository.deleteAll(tasks);
        }
        List<Image> images = imageRepository.findByProjectId(id);
        if (!images.isEmpty()) {
            imageRepository.deleteAll(images);
        }
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

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task was not found"));

        if (task.getProjectId() == null || !task.getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("Task does not belong to project");
        }

        List<String> taskIds = project.getTaskIds();
        if (taskIds != null) {
            taskIds.removeIf(id -> id.equals(taskId));
        }

        projectRepository.save(project);
        taskRepository.deleteById(taskId);
    }

    public Project updateContractor(String id, String contractorId, Double latitude, Double longitude){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found"));

        contractorRepository.findById(contractorId)
                .orElseThrow(() -> new RuntimeException("Contractor not found"));
        project.setContractor(contractorId);
        applyCoordinates(project, latitude, longitude);

        return projectRepository.save(project);
    }

    public Project updateAddress(String id, String address, Double latitude, Double longitude){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));

        project.setAddress(address);
        applyCoordinates(project, latitude, longitude);

        return projectRepository.save(project);
    }

    public Project updateName(String id, String name, Double latitude, Double longitude){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));

        project.setName(name);
        applyCoordinates(project, latitude, longitude);

        return projectRepository.save(project);
    }

    public Project updateBudget(String id, Double budget, Double latitude, Double longitude){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));

        validateBudget(budget);
        project.setBudget(budget);
        double workerratio = (budget / 2) / 1500;
        project.setNumber_of_workers((int) workerratio);
        applyCoordinates(project, latitude, longitude);

        return projectRepository.save(project);
    }

    public Project updateWorkers(String id, Integer workers, Double latitude, Double longitude){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));
        validateWorkers(workers);
        project.setNumber_of_workers(workers);
        applyCoordinates(project, latitude, longitude);
        return projectRepository.save(project);
    }

    public Project updateProgress(String id, Integer progress, Double latitude, Double longitude){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));
        validateProgress(progress);
        project.setProgress(progress);
        applyCoordinates(project, latitude, longitude);
        return projectRepository.save(project);
    }

    public Project updateEta(String id, Integer eta, Double latitude, Double longitude){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));
        validateEta(eta);
        project.setETA(eta);
        applyCoordinates(project, latitude, longitude);
        return projectRepository.save(project);
    }

    public Project updateFinishedStatus(String id, boolean finished) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));
        project.setFinished(finished);
        return projectRepository.save(project);
    }

    public Project removeContractor(String id){
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project was not found!"));
        project.setContractor(null);
        return projectRepository.save(project);
    }

    private void validateBudget(Double budget) {
        if (budget != null && budget < 0) {
            throw new IllegalArgumentException("budget must be >= 0");
        }
    }

    private void validateWorkers(Integer workers) {
        if (workers != null && workers < 0) {
            throw new IllegalArgumentException("workers must be >= 0");
        }
    }

    private void validateProgress(Integer progress) {
        if (progress != null && (progress < 0 || progress > 100)) {
            throw new IllegalArgumentException("progress must be between 0 and 100");
        }
    }

    private void validateEta(Integer eta) {
        if (eta != null && eta < 0) {
            throw new IllegalArgumentException("eta must be >= 0");
        }
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }
    }

    private void applyCoordinates(Project project, Double latitude, Double longitude) {
        validateCoordinates(latitude, longitude);
        if (latitude != null) {
            project.setLatitude(latitude);
        }
        if (longitude != null) {
            project.setLongitude(longitude);
        }
    }

}
