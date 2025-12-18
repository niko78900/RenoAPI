package com.example.HomeReno.controller.dto;

import java.util.List;

public record ProjectResponse(
        String id,
        String name,
        String address,
        Double budget,
        int progress,
        int numberOfWorkers,
        String contractorId,
        String contractorName,
        List<String> taskIds,
        int eta
) {}
