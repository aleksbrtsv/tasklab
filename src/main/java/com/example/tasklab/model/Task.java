package com.example.tasklab.model;

public class Task {

    final private long id;
    final private String title;
    final private TaskPriority priority;
    private TaskStatus status;

    public Task(long id, String title, TaskPriority priority) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = TaskStatus.NEW;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

}
