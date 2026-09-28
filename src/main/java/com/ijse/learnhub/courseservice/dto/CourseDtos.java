package com.ijse.learnhub.courseservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseDtos {

    public record CourseRequest(
            @NotBlank(message = "Course code is required") @Size(max = 20) String courseCode,
            @NotBlank(message = "Title is required") @Size(max = 150) String title,
            @Size(max = 1000) String description,
            @NotNull(message = "Duration is required") @Positive(message = "Duration must be more than 0 weeks") Integer durationWeeks,
            @NotNull(message = "Fee is required") @Positive(message = "Fee must be more than 0") BigDecimal fee
    ) {}

    public record CourseResponse(
            Long id,
            String courseCode,
            String title,
            String description,
            Integer durationWeeks,
            BigDecimal fee,
            LocalDateTime createdAt
    ) {}
}
