package com.example.tasklab.persistence;

import com.example.tasklab.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskEntity toEntity(Task task);

    default Task toDomain(TaskEntity entity) {
        if (entity == null) {
            return null;
        }

        return Task.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getPriority(),
                entity.getStatus()
        );
    }
}