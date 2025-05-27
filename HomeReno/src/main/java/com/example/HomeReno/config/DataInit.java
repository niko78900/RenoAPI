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
public class DataInit {


    @Bean
    CommandLineRunner dataInitialize(ProjectRepository projectRepository, TaskRepository taskRepository){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                List<Project> savedProject = projectRepository.saveAll(
                        List.of(//String name, Double budget, String selected_contractor, String address
                                new Project("2 Bedroom Renovation", 25000.0, "Anthony Smith", "128 E Indiana Ave", 2),
                                new Project("3 Bedroom Apartment renovation", 30000.0, "Anthony Smith", "114 E Indiana Ave", 3),
                                new Project("House Renovation", 25000.0, "Jerry Black", "205 E Reno Ave", 6)
                        )
                );
                List<Task> TaskListA = new ArrayList<>(
                        List.of(
                                new Task(savedProject.get(0), "Purchase Paint", "Finished"),
                                new Task(savedProject.get(0), "Paint the walls", "Working"),
                                new Task(savedProject.get(0), "Set up electrical circuitry", "Finished"),
                                new Task(savedProject.get(0), "Tile Bathroom floor", "Finished"),
                                new Task(savedProject.get(0), "Set up kitchen amenities", "Finished")
                        )
                );
                savedProject.get(0).setTaskList(TaskListA);
                savedProject.get(0).setProgress(45);

                List<Task> TaskListB = new ArrayList<>(
                        List.of(
                                new Task(savedProject.get(1), "Set up new kitchen amenities", "Working"),
                                new Task(savedProject.get(1), "Setup security solution", "Working"),
                                new Task(savedProject.get(1), "Set up electrical circuitry", "Finished"),
                                new Task(savedProject.get(1), "Tile Bathroom floors", "Finished"),
                                new Task(savedProject.get(1), "Decorate office", "Not Started"),
                                new Task(savedProject.get(1), "Paint Walls", "Finished")
                        )
                );
                savedProject.get(1).setTaskList(TaskListB);
                savedProject.get(1).setProgress(28);

                List<Task> TaskListC = new ArrayList<>(
                        List.of(
                                new Task(savedProject.get(2), "Set up kids playground", "Not Started"),
                                new Task(savedProject.get(2), "Purchase kids playground attractions", "Working"),
                                new Task(savedProject.get(2), "Plant a tree", "Working"),
                                new Task(savedProject.get(2), "Set up backyard", "Working"),
                                new Task(savedProject.get(2), "Clean Garage", "Finished"),
                                new Task(savedProject.get(2), "Build shed in the backyard", "Not Started")
                        )
                );
                savedProject.get(2).setTaskList(TaskListC);
                savedProject.get(2).setProgress(78);

                projectRepository.save(savedProject.get(1));
                projectRepository.save(savedProject.get(0));
                projectRepository.save(savedProject.get(2));
            }
        };
    }
}
