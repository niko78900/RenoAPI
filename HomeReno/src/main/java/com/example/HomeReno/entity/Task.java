package com.example.HomeReno.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
public class Task {

    @Id
    private String id;
    
    private String projectId;

    private String name;

    private String status; //Statuses available: "FINISHED" , "WORKING" , "NOT_STARTED"

    public Task() {
    }

    public Task(String projectId, String name, String status) {
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

    public void setStatus(String status) {
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

    public String getStatus() {
        return status;
    }
}
