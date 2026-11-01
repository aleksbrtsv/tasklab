package com.example.tasklab.dto;

import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;

public record TaskResponse(
        long id,
        String title,
        TaskPriority priority,
        TaskStatus status
) {
}
