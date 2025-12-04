package com.example.HomeReno.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "contractors")
public class Contractor {

    @Id
    private String id;

    private String fullName; 
    private Double price;      //per project
    private Expertise expertise;

    public enum Expertise {
        JUNIOR,
        APPRENTICE,
        SENIOR
    }

    public Contractor() {
    }

    public Contractor(String fullName, Double price, Expertise expertise) {
        this.fullName = fullName;
        this.price = price;
        this.expertise = expertise;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }
}
