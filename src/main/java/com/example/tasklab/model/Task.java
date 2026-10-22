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

    synchronized public TaskStatus getStatus() {
        return status;
    }

    synchronized public void start() {

        if (status != TaskStatus.NEW) {
            throw new IllegalStateException("Задача не может быть начата, текущий статус: " + status);
        }

        status = TaskStatus.IN_PROGRESS;
    }

    synchronized public void complete() {

        if (status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Задача не может быть выполнена, текущий статус: " + status);
        }

        status = TaskStatus.DONE;
    }

    synchronized public void fail() {

        if (status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Нельзя завершить задачу с ошибкой, текущий статус: " + status);
        }

        status = TaskStatus.FAILED;
    }

}
