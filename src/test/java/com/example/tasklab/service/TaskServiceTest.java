package com.example.tasklab.service;

import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;
import com.example.tasklab.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;
    private Task task;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
        task = new Task(1L, "Test", TaskPriority.LOW);
    }

    @Test
    void shouldCreateTask() {
        when(taskRepository.saveIfAbsent(any(Task.class)))
                .thenReturn(true);

        Task createdTask = taskService.createTask(
                1L, "Test", TaskPriority.LOW
        );

        assertEquals(TaskStatus.NEW, createdTask.getStatus());

        verify(taskRepository).saveIfAbsent(any(Task.class));
    }

    @Test
    void shouldStartTask() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.updateStatusIfCurrent(
                1L,
                TaskStatus.NEW,
                TaskStatus.IN_PROGRESS
        )).thenReturn(true);

        Task result = taskService.startTask(1L);

        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void shouldNotCompleteNewTask() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        assertThrows(
                IllegalStateException.class,
                () -> taskService.completeTask(1L)
        );

        assertEquals(TaskStatus.NEW, task.getStatus());

        verify(taskRepository, never()).updateStatusIfCurrent(
                anyLong(),
                any(TaskStatus.class),
                any(TaskStatus.class)
        );
    }

    @Test
    void shouldNotStartTaskTwice() {

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.updateStatusIfCurrent(
                1L,
                TaskStatus.NEW,
                TaskStatus.IN_PROGRESS
        )).thenReturn(true);

        taskService.startTask(1L);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

        assertThrows(IllegalStateException.class, () -> taskService.startTask(1L));

        verify(taskRepository, times(1)).updateStatusIfCurrent(
                1L,
                TaskStatus.NEW,
                TaskStatus.IN_PROGRESS
        );
    }

    @Test
    void shouldCompleteTask() {

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.updateStatusIfCurrent(
                1L,
                TaskStatus.NEW,
                TaskStatus.IN_PROGRESS
        )).thenReturn(true);

        when(taskRepository.updateStatusIfCurrent(
                1L,
                TaskStatus.IN_PROGRESS,
                TaskStatus.DONE
        )).thenReturn(true);

        taskService.startTask(1L);
        Task completeTask = taskService.completeTask(1L);

        assertEquals(TaskStatus.DONE, completeTask.getStatus());

        verify(taskRepository).updateStatusIfCurrent(
                1L,
                TaskStatus.NEW,
                TaskStatus.IN_PROGRESS
        );

        verify(taskRepository).updateStatusIfCurrent(
                1L,
                TaskStatus.IN_PROGRESS,
                TaskStatus.DONE
        );
    }

    @Test
    void shouldFailTask() {

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.updateStatusIfCurrent(
                1L,
                TaskStatus.NEW,
                TaskStatus.IN_PROGRESS
        )).thenReturn(true);

        when(taskRepository.updateStatusIfCurrent(
                1L,
                TaskStatus.IN_PROGRESS,
                TaskStatus.FAILED
        )).thenReturn(true);

        taskService.startTask(1L);
        Task failedTask = taskService.failTask(1L);

        assertEquals(TaskStatus.FAILED, failedTask.getStatus());

        verify(taskRepository).updateStatusIfCurrent(
                1L,
                TaskStatus.NEW,
                TaskStatus.IN_PROGRESS
        );

        verify(taskRepository).updateStatusIfCurrent(
                1L,
                TaskStatus.IN_PROGRESS,
                TaskStatus.FAILED
        );
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertTrue(taskService.findById(999L).isEmpty());

    }

    @Test
    void shouldThrowWhenTaskNotFound() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> taskService.startTask(999L));

    }

    @Test
    void shouldNotCreateDuplicateTask() {

        when(taskRepository.saveIfAbsent(any(Task.class)))
                .thenReturn(true, false);

        taskService.createTask(1L, "Test", TaskPriority.LOW);

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(1L, "Test", TaskPriority.LOW));

        verify(taskRepository, times(2)).saveIfAbsent(any(Task.class));
    }
}