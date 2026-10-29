package com.example.tasklab.repository;

import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskStatus;
import com.example.tasklab.persistence.TaskJpaRepository;
import com.example.tasklab.persistence.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

    private final TaskJpaRepository taskJpaRepository;

    public boolean saveIfAbsent(Task task) {
        return taskJpaRepository.insertIfAbsent(
                task.getId(),
                task.getTitle(),
                task.getPriority().name(),
                task.getStatus().name()
        ) == 1;
    }

    public Optional<Task> findById(long id) {
        return taskJpaRepository.findById(id)
                .map(TaskMapper::toDomain);
    }

    public List<Task> findAll() {
        return taskJpaRepository.findAll()
                .stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    public boolean deleteById(long id) {
        return taskJpaRepository.deleteIfExists(id) == 1;
    }

    public boolean updateStatusIfCurrent(long id, TaskStatus expectedStatus, TaskStatus newStatus) {
        return taskJpaRepository.updateStatusIfCurrent(
                id,
                expectedStatus.name(),
                newStatus.name()
        ) == 1;
    }
}
