package com.example.HomeReno.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
public class Task {

    @Id
    private String id;
    
    private String projectId;

    private String name;

    private Status status; // Statuses available: "FINISHED" , "WORKING" , "NOT_STARTED", "CANCELED"

    public enum Status {
        NOT_STARTED,
        WORKING,
        FINISHED,
        CANCELED
    }

    public Task() {
    }

    public Task(String projectId, String name, Status status) {
        this.projectId = projectId;
        this.name = name;
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }
}
