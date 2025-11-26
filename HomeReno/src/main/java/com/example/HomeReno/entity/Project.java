package com.example.HomeReno.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double budget;
    private String contractor;
    private String address;
    private int progress;
    private int number_of_workers;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonIgnore
    private List<Task> taskList;
    private int ETA;

    public Project() {
    }

    public Project(String name, Double budget, String selected_contractor, String address, int ETA) {
        this.taskList = new ArrayList<>();
        this.name = name;
        this.budget = budget;
        this.contractor = selected_contractor;
        this.address = address;
        this.progress = 0;
        double workerratio = (budget / 2) / 1500; //Dividing the budget by 2 and making sure that every worker can get paid at least 1500 USD for the job
        this.number_of_workers = (int) workerratio;
        this.ETA = ETA;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void addTaskToList(Task task){
        taskList.add(task);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setNumber_of_workers(int number_of_workers) {
        this.number_of_workers = number_of_workers;
    }

    public Long getId() {
        return id;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public String getName() {
        return name;
    }

    public Double getBudget() {
        return budget;
    }

    public String getContractor() {
        return contractor;
    }

    public int getProgress() {
        return progress;
    }

    public int getNumber_of_workers() {
        return number_of_workers;
    }
}
