package com.example.sms.dto;

import com.example.sms.model.Gender;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        Gender gender,
        String address,
        LocalDate enrollmentDate,
        Long courseId,
        String courseName
) {
}
