package com.example.HomeReno.service;

import com.example.HomeReno.entity.Image;
import com.example.HomeReno.entity.Project;
import com.example.HomeReno.repository.ImageRepository;
import com.example.HomeReno.repository.ProjectRepository;
import com.example.HomeReno.security.SecurityUtils;
import com.example.HomeReno.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    private static final int MAX_IMAGES_PER_PROJECT = 50;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<Image> getAllImages() {
        UserPrincipal currentUser = SecurityUtils.requireUser();
        if (currentUser.isAdmin()) {
            return imageRepository.findAll();
        }
        List<Project> projects = projectRepository.findByOwnerId(currentUser.getId());
        List<String> projectIds = projects.stream().map(Project::getId).toList();
        return projectIds.isEmpty() ? List.of() : imageRepository.findByProjectIdIn(projectIds);
    }

    public Optional<Image> getImageById(String id) {
        Optional<Image> image = imageRepository.findById(id);
        image.ifPresent(existing -> requireProjectAccess(existing.getProjectId()));
        return image;
    }

    public List<Image> getImagesByProjectId(String projectId) {
        requireProjectAccess(projectId);
        return imageRepository.findByProjectId(projectId);
    }

    public Image createImage(Image image) {
        validateImage(image);
        Project project = requireProjectAccess(image.getProjectId());
        ensureImageCapacity(project, null);
        UserPrincipal currentUser = SecurityUtils.requireUser();
        image.setUploadedBy(currentUser.getUsername());
        if (image.getUploadedAt() == null) {
            image.setUploadedAt(LocalDateTime.now());
        }
        Image savedImage = imageRepository.save(image);
        linkImageToProject(project, savedImage.getId());
        return savedImage;
    }

    public Image createImageUpload(String projectId, MultipartFile file, String description) {
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("projectId is required");
        }
        Project project = requireProjectAccess(projectId);
        ensureImageCapacity(project, null);
        String url = fileStorageService.storeImage(file);
        Image image = new Image();
        image.setProjectId(projectId);
        image.setUrl(url);
        image.setDescription(description);
        UserPrincipal currentUser = SecurityUtils.requireUser();
        image.setUploadedBy(currentUser.getUsername());
        image.setUploadedAt(LocalDateTime.now());
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
                    if (previousProjectId != null && !previousProjectId.isBlank()) {
                        requireProjectAccess(previousProjectId);
                    }
                    Project nextProject = requireProjectAccess(nextProjectId);
                    if (previousProjectId == null || !previousProjectId.equals(nextProjectId)) {
                        ensureImageCapacity(nextProject, existing.getId());
                    }

                    existing.setProjectId(nextProjectId);
                    existing.setUrl(image.getUrl());
                    existing.setDescription(image.getDescription());
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
            Project project = requireProjectAccess(projectId);
            List<String> imageIds = project.getImageIds();
            if (imageIds != null) {
                imageIds.removeIf(imageId -> imageId.equals(id));
                projectRepository.save(project);
            }
        }
        fileStorageService.deleteIfLocal(image.getUrl());
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

    private void ensureImageCapacity(Project project, String imageId) {
        List<String> imageIds = project.getImageIds();
        int size = imageIds == null ? 0 : imageIds.size();
        boolean alreadyLinked = imageId != null && imageIds != null && imageIds.contains(imageId);
        if (!alreadyLinked && size >= MAX_IMAGES_PER_PROJECT) {
            throw new IllegalArgumentException("project has reached image limit");
        }
    }

    private Project requireProjectAccess(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        SecurityUtils.requireProjectAccess(project);
        return project;
    }
}
