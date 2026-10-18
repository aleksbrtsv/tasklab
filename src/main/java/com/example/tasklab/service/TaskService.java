package com.example.tasklab.service;

import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(long id, String title, TaskPriority priority) {
        Task task = new Task(id, title, priority);
        taskRepository.save(task);
        return task;
    }

    public Optional<Task> findById(long id) {
        return taskRepository.findById(id);
    }

}

