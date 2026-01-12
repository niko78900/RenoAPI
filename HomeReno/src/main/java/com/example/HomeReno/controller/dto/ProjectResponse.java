package com.example.HomeReno.controller.dto;

import java.util.List;

public record ProjectResponse(
        String id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        Double budget,
        int progress,
        boolean finished,
        int numberOfWorkers,
        String contractorId,
        String contractorName,
        List<String> taskIds,
        List<String> imageIds,
        int eta
) {}
