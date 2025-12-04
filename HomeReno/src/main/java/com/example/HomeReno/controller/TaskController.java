package com.example.HomeReno.controller;

import com.example.HomeReno.entity.Task;
import com.example.HomeReno.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // -------------------------
    // GET ALL TASKS (OPTIONAL)
    // -------------------------
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // -------------------------
    // GET TASK BY ID
    // -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------
    // CREATE TASK DIRECTLY
    // -------------------------
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.saveTask(task);
    }

    // -------------------------
    // DELETE TASK
    // -------------------------
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
    }

    // -------------------------
    // GET TASKS BY PROJECT ID
    // -------------------------
    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable String projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

}
