package com.example.HomeReno.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    private String name;

    private String status; //Statuses available: "FINISHED" , "WORKING" , "NOT_STARTED"

    public Task(Project project, String name, String status) {
        this.project = project;
        this.name = name;
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
