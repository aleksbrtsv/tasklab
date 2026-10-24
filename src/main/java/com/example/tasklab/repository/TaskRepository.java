package com.example.tasklab.repository;

import com.example.tasklab.model.Task;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TaskRepository {

    private final Map<Long, Task> tasks = new HashMap<>();

    public synchronized boolean saveIfAbsent(Task task) {

        long taskId = task.getId();

        if (tasks.containsKey(taskId)) {
            return false;
        }

        tasks.put(taskId, task);
        return true;
    }

    public synchronized Optional<Task> findById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public synchronized List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public synchronized boolean deleteById(long id) {
        return tasks.remove(id) != null;
    }
}
