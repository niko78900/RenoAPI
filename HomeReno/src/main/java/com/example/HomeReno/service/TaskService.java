package com.example.HomeReno.service;


import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String Id){
        return taskRepository.findById(Id);
    }

    public Task saveTask(Task task) {
    return taskRepository.save(task);
    }
    
    public Task updateTask(String id, Task task) {
        return taskRepository.findById(id)
                .map(existing -> {
                    existing.setName(task.getName());
                    existing.setStatus(task.getStatus());
                    existing.setProjectId(task.getProjectId());
                    return taskRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    
    public void deleteTask(String id) {
    taskRepository.deleteById(id); 
    }
    
    public List<Task> getTasksByProjectId(String projectId) {
    return taskRepository.findByProjectId(projectId);
    }
}
