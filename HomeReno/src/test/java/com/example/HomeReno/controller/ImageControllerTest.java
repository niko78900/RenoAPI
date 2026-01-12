package com.example.HomeReno.controller;

import com.example.HomeReno.entity.Image;
import com.example.HomeReno.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@Import(ApiKeyFilter.class)
@TestPropertySource(properties = "api.key=test-key")
class ImageControllerTest {
    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String API_KEY = "test-key";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ImageService imageService;

    @Test
    void createImageReturns200() throws Exception {
        Image image = new Image();
        image.setId("image-id");
        image.setProjectId("project-id");
        image.setUrl("https://example.com/image.jpg");
        image.setDescription("Front elevation");
        image.setUploadedBy("tester");

        when(imageService.createImage(any(Image.class))).thenReturn(image);

        mockMvc.perform(post("/api/images")
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(image)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("image-id"))
                .andExpect(jsonPath("$.projectId").value("project-id"))
                .andExpect(jsonPath("$.url").value("https://example.com/image.jpg"));
    }

    @Test
    void getImagesByProjectReturnsList() throws Exception {
        Image image = new Image();
        image.setId("image-id");
        image.setProjectId("project-id");
        image.setUrl("https://example.com/image.jpg");

        when(imageService.getImagesByProjectId("project-id"))
                .thenReturn(List.of(image));

        mockMvc.perform(get("/api/images/project/{projectId}", "project-id")
                        .header(API_KEY_HEADER, API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("image-id"))
                .andExpect(jsonPath("$[0].projectId").value("project-id"));
    }
}
