package com.example.tasklab.dto;

import com.example.tasklab.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTaskRequest(
        @Positive long id,
        @NotBlank String title,
        @NotNull TaskPriority priority
) {
}