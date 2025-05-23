package com.example.HomeReno.service;


import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Optional<Task> getTaskById(Long Id){
        return taskRepository.findById(Id);
    }
}
