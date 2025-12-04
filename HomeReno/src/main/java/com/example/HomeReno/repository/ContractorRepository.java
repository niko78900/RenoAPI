package com.example.HomeReno.repository;

import com.example.HomeReno.entity.Contractor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ContractorRepository extends MongoRepository<Contractor, String> {

    List<Contractor> findByExpertise(Contractor.Expertise expertise);

    List<Contractor> findByFullNameContainingIgnoreCase(String name);

}
