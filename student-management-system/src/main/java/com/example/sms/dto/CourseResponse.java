package com.example.sms.dto;

public record CourseResponse(
        Long id,
        String courseCode,
        String name,
        String description,
        Integer durationInMonths
) {
}
