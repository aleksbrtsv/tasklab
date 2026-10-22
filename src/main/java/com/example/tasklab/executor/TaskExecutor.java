package com.example.tasklab.executor;

import com.example.tasklab.model.Task;
import com.example.tasklab.service.TaskService;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class TaskExecutor {

    private final TaskService taskService;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public TaskExecutor(TaskService taskService) {
        this.taskService = taskService;
    }

    public Future<Task> executeAsync(long taskId) {

        return executor.submit(() -> {
            taskService.startTask(taskId);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
            return taskService.completeTask(taskId);
        });

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
