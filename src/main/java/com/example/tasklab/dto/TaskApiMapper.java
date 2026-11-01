package com.example.tasklab.dto;

import com.example.tasklab.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskApiMapper {
    TaskResponse toResponse(Task task);
}
