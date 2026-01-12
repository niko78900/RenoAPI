package com.example.HomeReno.service;

import com.example.HomeReno.entity.Image;
import com.example.HomeReno.entity.Project;
import com.example.HomeReno.repository.ImageRepository;
import com.example.HomeReno.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public Optional<Image> getImageById(String id) {
        return imageRepository.findById(id);
    }

    public List<Image> getImagesByProjectId(String projectId) {
        return imageRepository.findByProjectId(projectId);
    }

    public Image createImage(Image image) {
        validateImage(image);
        Project project = projectRepository.findById(image.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (image.getUploadedAt() == null) {
            image.setUploadedAt(LocalDateTime.now());
        }
        Image savedImage = imageRepository.save(image);
        linkImageToProject(project, savedImage.getId());
        return savedImage;
    }

    public Image updateImage(String id, Image image) {
        validateImage(image);
        return imageRepository.findById(id)
                .map(existing -> {
                    String previousProjectId = existing.getProjectId();
                    String nextProjectId = image.getProjectId();
                    Project nextProject = projectRepository.findById(nextProjectId)
                            .orElseThrow(() -> new RuntimeException("Project not found"));

                    existing.setProjectId(nextProjectId);
                    existing.setUrl(image.getUrl());
                    existing.setDescription(image.getDescription());
                    existing.setUploadedBy(image.getUploadedBy());
                    if (image.getUploadedAt() != null) {
                        existing.setUploadedAt(image.getUploadedAt());
                    }
                    Image savedImage = imageRepository.save(existing);

                    if (previousProjectId != null && !previousProjectId.equals(nextProjectId)) {
                        projectRepository.findById(previousProjectId)
                                .ifPresent(project -> {
                                    List<String> imageIds = project.getImageIds();
                                    if (imageIds != null) {
                                        imageIds.removeIf(imageId -> imageId.equals(savedImage.getId()));
                                        projectRepository.save(project);
                                    }
                                });
                    }

                    linkImageToProject(nextProject, savedImage.getId());

                    return savedImage;
                })
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public void deleteImage(String id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        String projectId = image.getProjectId();
        if (projectId != null && !projectId.isBlank()) {
            projectRepository.findById(projectId)
                    .ifPresent(project -> {
                        List<String> imageIds = project.getImageIds();
                        if (imageIds != null) {
                            imageIds.removeIf(imageId -> imageId.equals(id));
                            projectRepository.save(project);
                        }
                    });
        }
        imageRepository.deleteById(id);
    }

    private void validateImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image is required");
        }
        String projectId = image.getProjectId();
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("projectId is required");
        }
        String url = image.getUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url is required");
        }
    }

    private void linkImageToProject(Project project, String imageId) {
        List<String> imageIds = project.getImageIds();
        if (imageIds == null) {
            imageIds = new ArrayList<>();
            project.setImageIds(imageIds);
        }
        if (!imageIds.contains(imageId)) {
            imageIds.add(imageId);
            projectRepository.save(project);
        }
    }
}
