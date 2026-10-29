package com.example.tasklab.model;

import lombok.Getter;

import java.util.Objects;

public class Task {

    @Getter
    private final long id;
    @Getter
    private final String title;
    @Getter
    private final TaskPriority priority;
    private TaskStatus status;

    public Task(long id, String title, TaskPriority priority) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = TaskStatus.NEW;
    }

    public synchronized TaskStatus getStatus() {
        return status;
    }

    public synchronized boolean tryStart() {
        if (status != TaskStatus.NEW) {
            return false;
        }

        status = TaskStatus.IN_PROGRESS;
        return true;
    }

    public synchronized boolean tryComplete() {
        if (status != TaskStatus.IN_PROGRESS) {
            return false;
        }

        status = TaskStatus.DONE;
        return true;
    }

    public synchronized boolean tryFail() {
        if (status != TaskStatus.IN_PROGRESS) {
            return false;
        }

        status = TaskStatus.FAILED;
        return true;
    }

    public static Task restore(long id, String title, TaskPriority priority, TaskStatus status) {
        Task task = new Task(id, title, priority);
        task.status = Objects.requireNonNull(status);
        return task;
    }
}