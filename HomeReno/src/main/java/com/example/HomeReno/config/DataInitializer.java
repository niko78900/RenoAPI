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
            Contractor c4 = new Contractor("Mila Petrovska", 950.0, Contractor.Expertise.SENIOR);
            Contractor c5 = new Contractor("Igor Trajkov", 700.0, Contractor.Expertise.APPRENTICE);

            contractorRepository.saveAll(List.of(c1, c2, c3, c4, c5));

            seedProject(
                    projectRepo,
                    taskRepo,
                    new Project("Home Renovation #1", 50000.0, c1.getId(), "Jurij Gagarin 74A", 3),
                    45,
                    List.of(
                            new TaskSeed("Clear rubble on plot", Task.Status.FINISHED),
                            new TaskSeed("Build foundation", Task.Status.WORKING),
                            new TaskSeed("Rough plumbing", Task.Status.NOT_STARTED),
                            new TaskSeed("Framing inspection", Task.Status.NOT_STARTED)
                    )
            );

            seedProject(
                    projectRepo,
                    taskRepo,
                    new Project("Kitchen Overhaul - Park Residence", 32000.0, c2.getId(), "Partizanska 11/3", 2),
                    30,
                    List.of(
                            new TaskSeed("Remove existing cabinetry", Task.Status.FINISHED),
                            new TaskSeed("Upgrade electrical circuits", Task.Status.WORKING),
                            new TaskSeed("Install quartz countertops", Task.Status.NOT_STARTED),
                            new TaskSeed("Appliance commissioning", Task.Status.NOT_STARTED)
                    )
            );

            seedProject(
                    projectRepo,
                    taskRepo,
                    new Project("Mountain Cabin Extension", 61000.0, c3.getId(), "Matka Canyon Road 5", 4),
                    55,
                    List.of(
                            new TaskSeed("Pour concrete piers", Task.Status.FINISHED),
                            new TaskSeed("Frame timber extension", Task.Status.WORKING),
                            new TaskSeed("Install panoramic windows", Task.Status.WORKING),
                            new TaskSeed("Interior insulation", Task.Status.NOT_STARTED)
                    )
            );

            seedProject(
                    projectRepo,
                    taskRepo,
                    new Project("Urban Loft Makeover", 45000.0, c4.getId(), "Dimitar Vlahov 27", 3),
                    20,
                    List.of(
                            new TaskSeed("Demolition and debris removal", Task.Status.FINISHED),
                            new TaskSeed("Soundproof ceiling panels", Task.Status.WORKING),
                            new TaskSeed("Custom steel staircase", Task.Status.NOT_STARTED),
                            new TaskSeed("Smart lighting setup", Task.Status.NOT_STARTED)
                    )
            );

            seedProject(
                    projectRepo,
                    taskRepo,
                    new Project("Lake House Energy Retrofit", 38000.0, c5.getId(), "Ohrid Lakeshore 88", 2),
                    65,
                    List.of(
                            new TaskSeed("Audit existing insulation", Task.Status.FINISHED),
                            new TaskSeed("Install triple-glazed windows", Task.Status.FINISHED),
                            new TaskSeed("Heat pump integration", Task.Status.WORKING),
                            new TaskSeed("Solar inverter calibration", Task.Status.NOT_STARTED)
                    )
            );

            seedProject(
                    projectRepo,
                    taskRepo,
                    new Project("Suburban Bathroom Upgrades", 21000.0, c1.getId(), "Blagoja Stefkovski 14", 1),
                    15,
                    List.of(
                            new TaskSeed("Demolition and plumbing rough-in", Task.Status.WORKING),
                            new TaskSeed("Waterproofing membrane install", Task.Status.NOT_STARTED),
                            new TaskSeed("Tile setting and grouting", Task.Status.NOT_STARTED),
                            new TaskSeed("Fixture installation & QA", Task.Status.NOT_STARTED)
                    )
            );

            System.out.println("Database initialized with diverse sample data.");
        };
    }

    private void seedProject(
            ProjectRepository projectRepo,
            TaskRepository taskRepo,
            Project project,
            int progress,
            List<TaskSeed> tasks
    ) {
        project.setProgress(progress);
        Project savedProject = projectRepo.save(project);

        List<String> taskIds = new ArrayList<>();
        for (TaskSeed seed : tasks) {
            Task task = new Task();
            task.setProjectId(savedProject.getId());
            task.setName(seed.getName());
            task.setStatus(seed.getStatus());
            task = taskRepo.save(task);
            taskIds.add(task.getId());
        }

        savedProject.setTaskIds(taskIds);
        projectRepo.save(savedProject);
    }

    private static class TaskSeed {
        private final String name;
        private final Task.Status status;

        TaskSeed(String name, Task.Status status) {
            this.name = name;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public Task.Status getStatus() {
            return status;
        }
    }
}
