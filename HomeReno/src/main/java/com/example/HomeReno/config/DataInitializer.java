package com.example.HomeReno.config;

import com.example.HomeReno.entity.Contractor;
import com.example.HomeReno.entity.Project;
import com.example.HomeReno.entity.Task;
import com.example.HomeReno.repository.ContractorRepository;
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
    CommandLineRunner initData(ProjectRepository projectRepo, TaskRepository taskRepo, ContractorRepository contractorRepository) {
        return args -> {
            // Clear DB
            projectRepo.deleteAll();
            taskRepo.deleteAll();
            contractorRepository.deleteAll();

            Contractor c1 = new Contractor("John Markovski", 1200.0, Contractor.Expertise.SENIOR);
            Contractor c2 = new Contractor("Elena Stojanova", 600.0, Contractor.Expertise.JUNIOR);
            Contractor c3 = new Contractor("Petar Ilievski", 850.0, Contractor.Expertise.APPRENTICE);

            contractorRepository.saveAll(List.of(c1, c2, c3));

            // Create example project
            // String name, Double budget, String selected_contractor, String address, int ETA
            Project p = new Project("Home Renovation #1", 50000.0, c1.getId(), "Jurij Gagarin 74A", 3);
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

            System.out.println("Database initialized with sample data.");
        };
    }
}
