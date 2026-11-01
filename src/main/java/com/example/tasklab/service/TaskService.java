package com.example.tasklab.service;

import com.example.tasklab.exception.TaskAlreadyExistsException;
import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;
import com.example.tasklab.persistence.TaskEntity;
import com.example.tasklab.persistence.TaskJpaRepository;
import com.example.tasklab.persistence.TaskMapper;
import com.example.tasklab.persistence.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public Task createTask(long id, String title, TaskPriority priority) {
        Task task = new Task(id, title, priority);

        int inserted = taskJpaRepository.insertIfAbsent(
                task.getId(),
                task.getTitle(),
                task.getPriority().name(),
                task.getStatus().name()
        );

        if (inserted != 1) {
            throw new TaskAlreadyExistsException("Задача уже сохранена");
        }

        return task;
    }

    @Transactional
    public void deleteTask(long id) {
        if (taskJpaRepository.deleteIfExists(id) != 1) {
            throw new IllegalArgumentException(
                    "Такой задачи не существует, ID: " + id
            );
        }
    }

    @Transactional
    public Task startTask(long id) {
        Task task = findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Задача не найдена, id=" + id
                ));

        if (!task.tryStart()) {
            throw new IllegalStateException(
                    "Задача не может быть начата, текущий статус: "
                            + task.getStatus()
            );
        }

        if (taskJpaRepository.updateStatusIfCurrent(
                id,
                TaskStatus.NEW.name(),
                TaskStatus.IN_PROGRESS.name()
        ) != 1) {
            throw new IllegalStateException(
                    "Задача не может быть начата, ID: " + id
            );
        }

        return task;
    }

    @Transactional
    public Task completeTask(long id) {
        Task task = findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Задача не найдена, id=" + id
                ));

        if (!task.tryComplete()) {
            throw new IllegalStateException(
                    "Задача не может быть завершена, текущий статус: "
                            + task.getStatus()
            );
        }

        if (taskJpaRepository.updateStatusIfCurrent(
                id,
                TaskStatus.IN_PROGRESS.name(),
                TaskStatus.DONE.name()
        ) != 1) {
            throw new IllegalStateException(
                    "Задача не может быть завершена, ID: " + id
            );
        }

        return task;
    }

    @Transactional
    public Task failTask(long id) {
        Task task = findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Задача не найдена, id=" + id
                ));

        if (!task.tryFail()) {
            throw new IllegalStateException(
                    "Задача не может быть завершена с ошибкой, текущий статус: "
                            + task.getStatus()
            );
        }

        if (taskJpaRepository.updateStatusIfCurrent(
                id,
                TaskStatus.IN_PROGRESS.name(),
                TaskStatus.FAILED.name()
        ) != 1) {
            throw new IllegalStateException(
                    "Задача не может быть завершена с ошибкой, ID: " + id
            );
        }

        return task;
    }

    @Transactional(readOnly = true)
    public Optional<Task> findById(long id) {
        return taskJpaRepository.findById(id)
                .map(taskMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<Task> searchTasks(
            String title,
            TaskStatus status,
            TaskPriority priority,
            Pageable pageable
    ) {
        Specification<TaskEntity> spec = Specification.unrestricted();

        if (title != null && !title.isBlank()) {
            spec = spec.and(
                    TaskSpecifications.titleContains(title.trim())
            );
        }

        if (status != null) {
            spec = spec.and(
                    TaskSpecifications.hasStatus(status)
            );
        }

        if (priority != null) {
            spec = spec.and(
                    TaskSpecifications.hasPriority(priority)
            );
        }

        return taskJpaRepository.findAll(spec, pageable)
                .map(taskMapper::toDomain);
    }
}