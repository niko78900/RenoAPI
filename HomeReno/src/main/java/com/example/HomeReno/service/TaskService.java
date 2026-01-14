package com.example.HomeReno.service;


import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import com.example.HomeReno.security.SecurityUtils;
import com.example.HomeReno.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<Task> getAllTasks(){
        UserPrincipal currentUser = SecurityUtils.requireUser();
        if (currentUser.isAdmin()) {
            return taskRepository.findAll();
        }
        List<Project> projects = projectRepository.findByOwnerId(currentUser.getId());
        List<String> projectIds = projects.stream().map(Project::getId).toList();
        return projectIds.isEmpty() ? List.of() : taskRepository.findByProjectIdIn(projectIds);
    }

    public Optional<Task> getTaskById(String Id){
        Optional<Task> task = taskRepository.findById(Id);
        task.ifPresent(existing -> requireProjectAccess(existing.getProjectId()));
        return task;
    }

    public Task saveTask(Task task) {
        if (task.getProjectId() == null || task.getProjectId().isBlank()) {
            throw new IllegalArgumentException("projectId is required");
        }
        Project project = requireProjectAccess(task.getProjectId());
        Task savedTask = taskRepository.save(task);
        List<String> taskIds = project.getTaskIds();
        if (taskIds == null) {
            taskIds = new ArrayList<>();
            project.setTaskIds(taskIds);
        }
        if (!taskIds.contains(savedTask.getId())) {
            taskIds.add(savedTask.getId());
            projectRepository.save(project);
        }
        return savedTask;
    }
    
    public Task updateTask(String id, Task task) {
        if (task.getProjectId() == null || task.getProjectId().isBlank()) {
            throw new IllegalArgumentException("projectId is required");
        }
        return taskRepository.findById(id)
                .map(existing -> {
                    String previousProjectId = existing.getProjectId();
                    String nextProjectId = task.getProjectId();
                    if (previousProjectId != null && !previousProjectId.isBlank()) {
                        requireProjectAccess(previousProjectId);
                    }
                    Project nextProject = requireProjectAccess(nextProjectId);

                    existing.setName(task.getName());
                    existing.setStatus(task.getStatus());
                    existing.setProjectId(nextProjectId);
                    Task savedTask = taskRepository.save(existing);

                    if (previousProjectId != null && !previousProjectId.equals(nextProjectId)) {
                        projectRepository.findById(previousProjectId)
                                .ifPresent(project -> {
                                    List<String> taskIds = project.getTaskIds();
                                    if (taskIds != null) {
                                        taskIds.removeIf(taskId -> taskId.equals(savedTask.getId()));
                                        projectRepository.save(project);
                                    }
                                });
                    }

                    List<String> taskIds = nextProject.getTaskIds();
                    if (taskIds == null) {
                        taskIds = new ArrayList<>();
                        nextProject.setTaskIds(taskIds);
                    }
                    if (!taskIds.contains(savedTask.getId())) {
                        taskIds.add(savedTask.getId());
                        projectRepository.save(nextProject);
                    }

                    return savedTask;
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    
    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        String projectId = task.getProjectId();
        if (projectId != null && !projectId.isBlank()) {
            Project project = requireProjectAccess(projectId);
            List<String> taskIds = project.getTaskIds();
            if (taskIds != null) {
                taskIds.removeIf(taskId -> taskId.equals(id));
                projectRepository.save(project);
            }
        }
        taskRepository.deleteById(id);
    }
    
    public List<Task> getTasksByProjectId(String projectId) {
        requireProjectAccess(projectId);
        return taskRepository.findByProjectId(projectId);
    }

    private Project requireProjectAccess(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        SecurityUtils.requireProjectAccess(project);
        return project;
    }
}
