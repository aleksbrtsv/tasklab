package com.example.tasklab.service;

import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;
import com.example.tasklab.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepository();
        taskService = new TaskService(taskRepository);
    }

    @Test
    void shouldCreateTask() {

        Task task = taskService.createTask(1, "Test", TaskPriority.LOW);

        assertEquals(1, task.getId());
        assertEquals("Test", task.getTitle());
        assertEquals(TaskPriority.LOW, task.getPriority());
        assertEquals(TaskStatus.NEW, task.getStatus());

        assertTrue(taskRepository.findById(task.getId()).isPresent());

    }

    @Test
    void shouldStartTask() {

        Task task = taskService.createTask(1, "Test", TaskPriority.LOW);
        taskService.startTask(task.getId());

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, taskRepository.findById(task.getId()).orElseThrow().getStatus());

    }

    @Test
    void shouldNotCompleteNewTask() {

        Task task = taskService.createTask(1, "Test", TaskPriority.LOW);

        assertThrows(IllegalStateException.class, () -> taskService.completeTask(task.getId()));
        assertEquals(TaskStatus.NEW, task.getStatus());

    }

    @Test
    void shouldNotStartTaskTwice() {

        Task task = taskService.createTask(1, "Test", TaskPriority.LOW);
        taskService.startTask(task.getId());

        assertThrows(IllegalStateException.class, () -> taskService.startTask(task.getId()));

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

    }

    @Test
    void shouldCompleteTask() {

        Task task = taskService.createTask(1, "Test", TaskPriority.LOW);
        taskService.startTask(task.getId());
        taskService.completeTask(task.getId());

        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals(TaskStatus.DONE, taskRepository.findById(task.getId()).orElseThrow().getStatus());

    }

    @Test
    void shouldFailTask() {

        Task task = taskService.createTask(1, "Test", TaskPriority.LOW);
        taskService.startTask(task.getId());
        taskService.failTask(task.getId());

        assertEquals(TaskStatus.FAILED, task.getStatus());
        assertEquals(TaskStatus.FAILED, taskRepository.findById(task.getId()).orElseThrow().getStatus());

    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {

        assertTrue(taskService.findById(999).isEmpty());

    }

    @Test
    void shouldThrowWhenTaskNotFound() {

        assertThrows(NoSuchElementException.class, () -> taskService.startTask(999));

    }

    @Test
    void shouldNotCreateDuplicateTask() {

        Task task = taskService.createTask(1, "Test", TaskPriority.LOW);
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(1, "Test", TaskPriority.LOW));
        assertSame(task, taskService.findById(1).orElseThrow());

    }
}