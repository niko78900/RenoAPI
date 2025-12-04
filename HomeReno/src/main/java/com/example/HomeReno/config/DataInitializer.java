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
            Project p = new Project();
            p.setName("Example Renovation");
            p.setContractor("John Doe");
            p.setAddress("123 Main Street");
            p.setBudget(5000.0);
            p.setETA(3);
            p.setProgress(10);

            // Save project to get ID
            p = projectRepo.save(p);

            // Create tasks
            Task t1 = new Task();
            t1.setProjectId(p.getId());
            t1.setName("Demolition");

            Task t2 = new Task();
            t2.setProjectId(p.getId());
            t2.setName("Install Cabinets");

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
