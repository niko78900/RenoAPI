package com.example.HomeReno.service;


import com.example.HomeReno.entity.Image;
import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.ContractorRepository;
import com.example.HomeReno.repository.ImageRepository;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import com.example.HomeReno.security.SecurityUtils;
import com.example.HomeReno.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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

    @Autowired
    private FileStorageService fileStorageService;

    public List<Project> getAllProjects(){
        UserPrincipal currentUser = SecurityUtils.requireUser();
        if (currentUser.isAdmin()) {
            return projectRepository.findAll();
        }
        return projectRepository.findByOwnerId(currentUser.getId());
    }

    public Project createProject(Project project){
        UserPrincipal currentUser = SecurityUtils.requireUser();
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
        project.setOwnerId(currentUser.getId());
        project.setTaskIds(new ArrayList<>());
        project.setImageIds(new ArrayList<>());
        return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(@NonNull String id){
        Optional<Project> project = projectRepository.findById(id);
        project.ifPresent(SecurityUtils::requireProjectAccess);
        return project;
    }

    public Optional<Project> getProjectByName(@NonNull String name){
        Optional<Project> project = projectRepository.findByName(name);
        project.ifPresent(SecurityUtils::requireProjectAccess);
        return project;
    }

    public List<Project> getProjectsByContractorId(@NonNull String contractorId){
        UserPrincipal currentUser = SecurityUtils.requireUser();
        if (currentUser.isAdmin()) {
            return projectRepository.findByContractorId(contractorId);
        }
        return projectRepository.findByContractorIdAndOwnerId(contractorId, currentUser.getId());
    }

    public Optional<Project> getProjectByAddress(@NonNull String address){
        Optional<Project> project = projectRepository.findByAddress(address);
        project.ifPresent(SecurityUtils::requireProjectAccess);
        return project;
    }

    public Map<String, Object> getTimeline(@NonNull String id){
        Project project = requireProjectAccess(id);

        Map<String, Object> response = new HashMap<>();
        response.put("ID", project.getId());
        response.put("Name", project.getName());
        response.put("Progress", project.getProgress() + " %");
        response.put("Task List", project.getTaskIds());
        response.put("Estimated Time to finish: ", project.getETA() + " Months");

        return response;
    }

    public boolean getFinishedStatus(@NonNull String id) {
        Project project = requireProjectAccess(id);
        return project.isFinished();
    }

    public void deleteProject(@NonNull String id){
        requireProjectAccess(id);
        List<Task> tasks = taskRepository.findByProjectId(id);
        if (!tasks.isEmpty()) {
            taskRepository.deleteAll(tasks);
        }
        List<Image> images = imageRepository.findByProjectId(id);
        if (!images.isEmpty()) {
            for (Image image : images) {
                fileStorageService.deleteIfLocal(image.getUrl());
            }
            imageRepository.deleteAll(images);
        }
        projectRepository.deleteById(id);
    }

    public Project addTaskToProject(@NonNull String projectId, Task task) {
        Project project = requireProjectAccess(projectId);
        task.setProjectId(projectId);
        Task savedTask = taskRepository.save(task);
        List<String> taskIds = project.getTaskIds();
        if (taskIds == null) {
            taskIds = new ArrayList<>();
            project.setTaskIds(taskIds);
        }
        taskIds.add(savedTask.getId());
        return projectRepository.save(project);
    }


    public void removeTaskFromProject(@NonNull String projectId, @NonNull String taskId){
        Project project = requireProjectAccess(projectId);

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

    public Project updateContractor(@NonNull String id, @NonNull String contractorId, Double latitude, Double longitude){
        Project project = requireProjectAccess(id);

        contractorRepository.findById(contractorId)
                .orElseThrow(() -> new RuntimeException("Contractor not found"));
        project.setContractor(contractorId);
        applyCoordinates(project, latitude, longitude);

        return projectRepository.save(project);
    }

    public Project updateAddress(@NonNull String id, @NonNull String address, Double latitude, Double longitude){
        Project project = requireProjectAccess(id);

        project.setAddress(address);
        applyCoordinates(project, latitude, longitude);

        return projectRepository.save(project);
    }

    public Project updateName(@NonNull String id, @NonNull String name, Double latitude, Double longitude){
        Project project = requireProjectAccess(id);

        project.setName(name);
        applyCoordinates(project, latitude, longitude);

        return projectRepository.save(project);
    }

    public Project updateBudget(@NonNull String id, Double budget, Double latitude, Double longitude){
        Project project = requireProjectAccess(id);

        validateBudget(budget);
        project.setBudget(budget);
        double workerratio = (budget / 2) / 1500;
        project.setNumber_of_workers((int) workerratio);
        applyCoordinates(project, latitude, longitude);

        return projectRepository.save(project);
    }

    public Project updateWorkers(@NonNull String id, Integer workers, Double latitude, Double longitude){
        Project project = requireProjectAccess(id);
        validateWorkers(workers);
        project.setNumber_of_workers(workers);
        applyCoordinates(project, latitude, longitude);
        return projectRepository.save(project);
    }

    public Project updateProgress(@NonNull String id, Integer progress, Double latitude, Double longitude){
        Project project = requireProjectAccess(id);
        validateProgress(progress);
        project.setProgress(progress);
        applyCoordinates(project, latitude, longitude);
        return projectRepository.save(project);
    }

    public Project updateEta(@NonNull String id, Integer eta, Double latitude, Double longitude){
        Project project = requireProjectAccess(id);
        validateEta(eta);
        project.setETA(eta);
        applyCoordinates(project, latitude, longitude);
        return projectRepository.save(project);
    }

    public Project updateFinishedStatus(@NonNull String id, boolean finished) {
        Project project = requireProjectAccess(id);
        project.setFinished(finished);
        return projectRepository.save(project);
    }

    public Project removeContractor(@NonNull String id){
        Project project = requireProjectAccess(id);
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

    private Project requireProjectAccess(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        SecurityUtils.requireProjectAccess(project);
        return project;
    }

}
