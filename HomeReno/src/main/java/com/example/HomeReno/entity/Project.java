package com.example.HomeReno.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "projects")
public class Project {

    @Id
    private String id;
    
    private String name;
    private Double budget;
    private String contractorId;   // references Contractor._id
    private String address;
    private Double latitude;
    private Double longitude;
    private int progress;
    private int number_of_workers;
    private boolean finished;

    private List<String> taskIds = new ArrayList<>();
    @JsonProperty("eta")
    @JsonAlias("ETA")
    private int ETA;

    public Project() {
    }

    public Project(String name, Double budget, String selected_contractor, String address, int ETA) {
        this.name = name;
        this.budget = budget;
        this.contractorId = selected_contractor;
        this.address = address;
        this.progress = 0;
        double workerratio = (budget / 2) / 1500; //Dividing the budget by 2 and making sure that every worker can get paid at least 1500 USD for the job
        this.number_of_workers = (int) workerratio;
        this.ETA = ETA;
        this.finished = false;
    }

    public void setETA(int ETA) {
        this.ETA = ETA;
    }

    public int getETA() {
        return ETA;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }

    public void addTaskToList(String taskId){
        taskIds.add(taskId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public void setContractor(String contractor) {
        this.contractorId = contractor;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setNumber_of_workers(int number_of_workers) {
        this.number_of_workers = number_of_workers;
    }

    public String getId() {
        return id;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public String getName() {
        return name;
    }

    public Double getBudget() {
        return budget;
    }

    public String getContractor() {
        return contractorId;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getNumber_of_workers() {
        return number_of_workers;
    }
}
