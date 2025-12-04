package com.example.HomeReno.service;

import com.example.HomeReno.entity.Contractor;
import com.example.HomeReno.repository.ContractorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractorService {

    @Autowired
    private ContractorRepository contractorRepository;

    public List<Contractor> getAllContractors() {
        return contractorRepository.findAll();
    }

    public Optional<Contractor> getContractorById(String id) {
        return contractorRepository.findById(id);
    }

    public Contractor createContractor(Contractor contractor) {
        return contractorRepository.save(contractor);
    }

    public void deleteContractor(String id) {
        contractorRepository.deleteById(id);
    }

    public List<Contractor> findByExpertise(Contractor.Expertise expertise) {
        return contractorRepository.findByExpertise(expertise);
    }

    public List<Contractor> searchByName(String name) {
        return contractorRepository.findByFullNameContainingIgnoreCase(name);
    }
}
