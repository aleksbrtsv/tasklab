package com.example.tasklab.repository;

import com.example.tasklab.model.Task;

import java.util.*;

public class TaskRepository {

    private final Map<Long, Task> tasks = new HashMap<>();

    public void save(Task task) {

        long taskId = task.getId();

        if (tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("Задача уже сохранена");
        }

        tasks.put(taskId, task);
    }

    public Optional<Task> findById(long id) {

        return Optional.ofNullable(tasks.get(id));
    }

    public List<Task> findAll() {

        return new ArrayList<>(tasks.values());
    }

    public void deleteById(long id) {

        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Такой задачи не существует, ID: " + id);
        }

        tasks.remove(id);
    }
}
