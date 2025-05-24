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
                                new Project("Sport Center Renovation", 95000.0, "Anthony Smith", "517 W Arbor Ave", 4),
                                new Project("Mall Renovation", 1500000.0, "Anthony Smith", "706 Kirkwood Mall", 9),
                                new Project("Park Renovation", 25000.0, "Jerry Black", "205 E Reno Ave", 1)
                        )
                );
                List<Task> TaskListA = new ArrayList<>(
                        List.of(
                                new Task(savedProject.get(0), "Purchase Sports Equipment", "Finished"),
                                new Task(savedProject.get(0), "Store Sports Equipment", "Working"),
                                new Task(savedProject.get(0), "Set up electrical circuitry", "Finished"),
                                new Task(savedProject.get(0), "Tile Female Bathroom floor", "Finished"),
                                new Task(savedProject.get(0), "Tile Male Bathroom floor", "Finished")
                        )
                );
                savedProject.get(0).setTaskList(TaskListA);
                savedProject.get(0).setProgress(45);

                List<Task> TaskListB = new ArrayList<>(
                        List.of(
                                new Task(savedProject.get(1), "Sell commercial space", "Working"),
                                new Task(savedProject.get(1), "Setup security solution", "Working"),
                                new Task(savedProject.get(1), "Set up electrical circuitry", "Finished"),
                                new Task(savedProject.get(1), "Tile Female Bathroom floors", "Finished"),
                                new Task(savedProject.get(1), "Tile Male Bathroom floors", "Finished"),
                                new Task(savedProject.get(1), "Paint Walls", "Finished")
                        )
                );
                savedProject.get(1).setTaskList(TaskListB);
                savedProject.get(1).setProgress(28);

                List<Task> TaskListC = new ArrayList<>(
                        List.of(
                                new Task(savedProject.get(2), "Set up kids playground", "Not Started"),
                                new Task(savedProject.get(2), "Purchase kids playground attractions", "Working"),
                                new Task(savedProject.get(2), "Plant Trees", "Working"),
                                new Task(savedProject.get(2), "Set up benches", "Finished"),
                                new Task(savedProject.get(2), "Pave pathways", "Finished"),
                                new Task(savedProject.get(2), "Place Street Lamps", "Finished")
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
