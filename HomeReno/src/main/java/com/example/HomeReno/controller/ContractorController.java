package com.example.HomeReno.controller;

import com.example.HomeReno.entity.Contractor;
import com.example.HomeReno.service.ContractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contractors")
public class ContractorController {

    @Autowired
    private ContractorService contractorService;

    // -------------------------
    // GET ALL CONTRACTORS
    // -------------------------
    @GetMapping
    public List<Contractor> getAllContractors() {
        return contractorService.getAllContractors();
    }

    // -------------------------
    // GET BY ID
    // -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Contractor> getContractorById(@PathVariable String id) {
        return contractorService.getContractorById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------
    // CREATE CONTRACTOR
    // -------------------------
    @PostMapping
    public Contractor createContractor(@RequestBody Contractor contractor) {
        return contractorService.createContractor(contractor);
    }

    // -------------------------
    // DELETE CONTRACTOR
    // -------------------------
    @DeleteMapping("/{id}")
    public void deleteContractor(@PathVariable String id) {
        contractorService.deleteContractor(id);
    }

    // -------------------------
    // SEARCH BY NAME
    // -------------------------
    @GetMapping("/search/{name}")
    public List<Contractor> searchByName(@PathVariable String name) {
        return contractorService.searchByName(name);
    }

    // -------------------------
    // FILTER BY EXPERTISE
    // -------------------------
    @GetMapping("/expertise/{level}")
    public List<Contractor> getByExpertise(@PathVariable Contractor.Expertise level) {
        return contractorService.findByExpertise(level);
    }
}
