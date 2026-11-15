package com.example.sms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank(message = "is required") @Size(max = 20) String courseCode,
        @NotBlank(message = "is required") @Size(max = 100) String name,
        @Size(max = 500) String description,
        @NotNull(message = "is required") @Min(value = 1, message = "must be at least 1 month") Integer durationInMonths
) {
}
