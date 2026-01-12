package com.example.HomeReno.service;

import com.example.HomeReno.entity.Contractor;
import com.example.HomeReno.entity.Project;
import com.example.HomeReno.repository.ContractorRepository;
import com.example.HomeReno.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractorService {

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<Contractor> getAllContractors() {
        return contractorRepository.findAll();
    }

    public Optional<Contractor> getContractorById(String id) {
        return contractorRepository.findById(id);
    }

    public Contractor createContractor(Contractor contractor) {
        validateContractor(contractor);
        return contractorRepository.save(contractor);
    }

    public Contractor updateContractor(String id, Contractor contractor) {
        validateContractor(contractor);
        return contractorRepository.findById(id)
                .map(existing -> {
                    existing.setFullName(contractor.getFullName());
                    existing.setPrice(contractor.getPrice());
                    existing.setExpertise(contractor.getExpertise());
                    return contractorRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Contractor not found"));
    }

    public void deleteContractor(String id) {
        List<Project> projects = projectRepository.findByContractorId(id);
        for (Project project : projects) {
            project.setContractor(null);
        }
        if (!projects.isEmpty()) {
            projectRepository.saveAll(projects);
        }
        contractorRepository.deleteById(id);
    }

    public List<Contractor> findByExpertise(Contractor.Expertise expertise) {
        return contractorRepository.findByExpertise(expertise);
    }

    public List<Contractor> searchByName(String name) {
        return contractorRepository.findByFullNameContainingIgnoreCase(name);
    }

    private void validateContractor(Contractor contractor) {
        if (contractor == null) {
            throw new IllegalArgumentException("contractor is required");
        }
        String fullName = contractor.getFullName();
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("fullName is required");
        }
        if (contractor.getExpertise() == null) {
            throw new IllegalArgumentException("expertise is required");
        }
        Double price = contractor.getPrice();
        if (price == null || price < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
    }
}
