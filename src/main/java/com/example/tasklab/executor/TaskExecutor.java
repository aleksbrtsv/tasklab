package com.example.tasklab.executor;

import com.example.tasklab.model.Task;
import com.example.tasklab.service.TaskService;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.*;

@Service
public class TaskExecutor {

    private final TaskService taskService;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Set<Long> inFlightTaskIds = ConcurrentHashMap.newKeySet();

    public TaskExecutor(TaskService taskService) {
        this.taskService = taskService;
    }

    public Future<Task> executeAsync(long taskId) {

        if (!inFlightTaskIds.add(taskId)) {
            throw new IllegalStateException(
                    "Задача с ID " + taskId + " уже выполняется"
            );
        }

        try {
            return executor.submit(() -> processTask(taskId));
        } catch (RejectedExecutionException e) {
            inFlightTaskIds.remove(taskId);
            throw e;
        }
    }

    private Task processTask(long taskId) throws InterruptedException {
        try {
            taskService.startTask(taskId);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }

            return taskService.completeTask(taskId);

        } finally {
            inFlightTaskIds.remove(taskId);
        }
    }

    public Set<Long> getInFlightTaskIds() {
        return Set.copyOf(inFlightTaskIds);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}