package com.example.tasklab.service;

import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(long id, String title, TaskPriority priority) {
        Task task = new Task(id, title, priority);
        if (!taskRepository.saveIfAbsent(task)) {
            throw new IllegalArgumentException("Задача уже сохранена");
        }
        return task;
    }

    public void deleteTask(long id) {
        if (!taskRepository.deleteById(id)) {
            throw new IllegalArgumentException("Такой задачи не существует, ID: " + id);
        }
    }

    public Optional<Task> findById(long id) {
        return taskRepository.findById(id);
    }

    public Task startTask(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Задача не найдена, id=" + id));

        if (!task.tryStart()) {
            throw new IllegalStateException(
                    "Задача не может быть начата, текущий статус: " + task.getStatus()
            );
        }

        return task;
    }

    public Task completeTask(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Задача не найдена, id=" + id));

        if (!task.tryComplete()) {
            throw new IllegalStateException(
                    "Задача не может быть завершена, текущий статус: " + task.getStatus()
            );
        }

        return task;
    }

    public Task failTask(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Задача не найдена, id=" + id));

        if (!task.tryFail()) {
            throw new IllegalStateException(
                    "Задача не может быть завершена с ошибкой, текущий статус: " + task.getStatus()
            );
        }

        return task;
    }
}