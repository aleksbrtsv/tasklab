package com.example.tasklab.persistence;

import com.example.tasklab.model.Task;

public class TaskMapper {

    public static TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.getStatus()
        );
    }
    public static Task toDomain(TaskEntity entity) {
        return Task.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getPriority(),
                entity.getStatus()
        );
    }
}
