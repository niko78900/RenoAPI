package com.example.HomeReno.controller;

import com.example.HomeReno.entity.Contractor;
import com.example.HomeReno.service.ContractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contractors")
@CrossOrigin(origins = "http://localhost:4200")
public class ContractorController {

    @Autowired
    private ContractorService contractorService;
    @GetMapping
    public List<Contractor> getAllContractors() {
        return contractorService.getAllContractors();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Contractor> getContractorById(@PathVariable String id) {
        return contractorService.getContractorById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Contractor> createContractor(@RequestBody Contractor contractor) {
        try {
            return ResponseEntity.ok(contractorService.createContractor(contractor));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contractor> updateContractor(@PathVariable String id, @RequestBody Contractor contractor) {
        try {
            return ResponseEntity.ok(contractorService.updateContractor(id, contractor));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public void deleteContractor(@PathVariable String id) {
        contractorService.deleteContractor(id);
    }
    @GetMapping("/search/{name}")
    public List<Contractor> searchByName(@PathVariable String name) {
        return contractorService.searchByName(name);
    }
    @GetMapping("/expertise/{level}")
    public List<Contractor> getByExpertise(@PathVariable Contractor.Expertise level) {
        return contractorService.findByExpertise(level);
    }
    @GetMapping("/expertise")
    public Contractor.Expertise[] listExpertiseLevels() {
        return Contractor.Expertise.values();
    }
}
