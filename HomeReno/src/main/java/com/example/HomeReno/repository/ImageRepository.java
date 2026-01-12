package com.example.HomeReno.repository;

import com.example.HomeReno.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImageRepository extends MongoRepository<Image, String> {
    List<Image> findByProjectId(String projectId);
}
