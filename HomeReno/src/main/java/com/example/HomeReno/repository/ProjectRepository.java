package com.example.HomeReno.repository;

import com.example.HomeReno.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    public Optional<Project> findByName(String name);
    List<Project> findByContractorId(String contractorId);
    List<Project> findByOwnerId(String ownerId);
    List<Project> findByContractorIdAndOwnerId(String contractorId, String ownerId);
    public Optional<Project> findByAddress(String address);
}
