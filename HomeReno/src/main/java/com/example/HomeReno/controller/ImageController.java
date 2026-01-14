package com.example.HomeReno.controller;

import com.example.HomeReno.entity.Image;
import com.example.HomeReno.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:4200")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @GetMapping
    public List<Image> getAllImages() {
        return imageService.getAllImages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable String id) {
        return imageService.getImageById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    public List<Image> getImagesByProjectId(@PathVariable String projectId) {
        return imageService.getImagesByProjectId(projectId);
    }

    @PostMapping
    public ResponseEntity<Image> createImage(@RequestBody Image image) {
        try {
            return ResponseEntity.ok(imageService.createImage(image));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Image> uploadImage(@RequestParam("projectId") String projectId,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "description", required = false) String description) {
        try {
            return ResponseEntity.ok(imageService.createImageUpload(projectId, file, description));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable String id, @RequestBody Image image) {
        try {
            return ResponseEntity.ok(imageService.updateImage(id, image));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable String id) {
        try {
            imageService.deleteImage(id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
