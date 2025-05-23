package com.example.HomeReno.config;


import com.example.HomeReno.entity.Project;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInit {


    @Bean
    CommandLineRunner dataInitialize(ProjectRepository projectRepository, TaskRepository taskRepository){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                List<Project> savedProject = projectRepository.saveAll(
                        List.of(//String name, Double budget, String selected_contractor, String address
                                new Project("Sport Center Renovation", 95000.0, "Anthony Smith", "517 W Arbor Ave", 4),
                                new Project("Mall Renovation", 1500000.0, "Anthony Smith", "706 Kirkwood Mall", 9),
                                new Project("Park Renovation", 250000.0, "Anthony Smith", "205 E Reno Ave", 1)
                        )
                );
            }
        };
    }
}
