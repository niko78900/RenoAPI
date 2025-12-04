package com.example.HomeReno.config;

import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(ProjectRepository projectRepo, TaskRepository taskRepo) {
        return args -> {

            // Clear DB
            projectRepo.deleteAll();
            taskRepo.deleteAll();

            // Create example project
            // String name, Double budget, String selected_contractor, String address, int ETA
            Project p = new Project("Home Renovation #1", 50000.0, "John Doe", "Jurij Gagarin 74A", 3);
            p.setETA(3);

            // Save project to get ID
            p = projectRepo.save(p);

            // Create tasks
            Task t1 = new Task();
            t1.setProjectId(p.getId());
            t1.setName("Clear rubble on plot");

            Task t2 = new Task();
            t2.setProjectId(p.getId());
            t2.setName("Build Foundation");

            // Save tasks
            t1 = taskRepo.save(t1);
            t2 = taskRepo.save(t2);

            // Assign tasks to project
            List<String> taskIds = new ArrayList<>();
            taskIds.add(t1.getId());
            taskIds.add(t2.getId());

            p.setTaskIds(taskIds);
            projectRepo.save(p);

            System.out.println("✔ Database initialized with sample data.");
        };
    }
}
