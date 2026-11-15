package com.example.sms.dto;

import com.example.sms.model.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record StudentRequest(
        @NotBlank(message = "is required") @Size(max = 50) String firstName,
        @NotBlank(message = "is required") @Size(max = 50) String lastName,
        @NotBlank(message = "is required") @Email @Size(max = 100) String email,
        @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "must be 7-15 characters (digits, +, -, space)") String phone,
        @NotNull(message = "is required") @Past(message = "must be in the past") LocalDate dateOfBirth,
        @NotNull(message = "is required (MALE, FEMALE or OTHER)") Gender gender,
        @Size(max = 255) String address,
        @NotNull(message = "is required") LocalDate enrollmentDate,
        Long courseId
) {
}
