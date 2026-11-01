package com.example.tasklab.service;

import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;
import com.example.tasklab.persistence.TaskEntity;
import com.example.tasklab.persistence.TaskJpaRepository;
import com.example.tasklab.persistence.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskJpaRepository taskJpaRepository;
    private TaskMapper taskMapper;
    private TaskService taskService;
    private Task task;

    @BeforeEach
    void setUp() {
        taskJpaRepository = mock(TaskJpaRepository.class);
        taskMapper = mock(TaskMapper.class);

        taskService = new TaskService(taskJpaRepository, taskMapper);
        task = new Task(1L, "Test", TaskPriority.LOW);
    }

    private void mockFoundTask() {
        TaskEntity entity = new TaskEntity(
                1L,
                "Test",
                TaskPriority.LOW,
                TaskStatus.NEW
        );

        when(taskJpaRepository.findById(1L))
                .thenReturn(Optional.of(entity));

        when(taskMapper.toDomain(entity))
                .thenReturn(task);
    }

    @Test
    void shouldCreateTask() {
        when(taskJpaRepository.insertIfAbsent(
                1L, "Test", "LOW", "NEW"
        )).thenReturn(1);

        Task createdTask = taskService.createTask(
                1L, "Test", TaskPriority.LOW
        );

        assertEquals(TaskStatus.NEW, createdTask.getStatus());

        verify(taskJpaRepository).insertIfAbsent(
                1L, "Test", "LOW", "NEW"
        );
    }

    @Test
    void shouldStartTask() {
        mockFoundTask();

        when(taskJpaRepository.updateStatusIfCurrent(
                1L, "NEW", "IN_PROGRESS"
        )).thenReturn(1);

        Task result = taskService.startTask(1L);

        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void shouldNotCompleteNewTask() {
        mockFoundTask();

        assertThrows(
                IllegalStateException.class,
                () -> taskService.completeTask(1L)
        );

        assertEquals(TaskStatus.NEW, task.getStatus());

        verify(taskJpaRepository, never()).updateStatusIfCurrent(
                anyLong(), anyString(), anyString()
        );
    }

    @Test
    void shouldNotStartTaskTwice() {
        mockFoundTask();

        when(taskJpaRepository.updateStatusIfCurrent(
                1L, "NEW", "IN_PROGRESS"
        )).thenReturn(1);

        taskService.startTask(1L);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

        assertThrows(
                IllegalStateException.class,
                () -> taskService.startTask(1L)
        );

        verify(taskJpaRepository, times(1)).updateStatusIfCurrent(
                1L, "NEW", "IN_PROGRESS"
        );
    }

    @Test
    void shouldCompleteTask() {
        mockFoundTask();

        when(taskJpaRepository.updateStatusIfCurrent(
                1L, "NEW", "IN_PROGRESS"
        )).thenReturn(1);

        when(taskJpaRepository.updateStatusIfCurrent(
                1L, "IN_PROGRESS", "DONE"
        )).thenReturn(1);

        taskService.startTask(1L);
        Task completedTask = taskService.completeTask(1L);

        assertEquals(TaskStatus.DONE, completedTask.getStatus());

        verify(taskJpaRepository).updateStatusIfCurrent(
                1L, "NEW", "IN_PROGRESS"
        );

        verify(taskJpaRepository).updateStatusIfCurrent(
                1L, "IN_PROGRESS", "DONE"
        );
    }

    @Test
    void shouldFailTask() {
        mockFoundTask();

        when(taskJpaRepository.updateStatusIfCurrent(
                1L, "NEW", "IN_PROGRESS"
        )).thenReturn(1);

        when(taskJpaRepository.updateStatusIfCurrent(
                1L, "IN_PROGRESS", "FAILED"
        )).thenReturn(1);

        taskService.startTask(1L);
        Task failedTask = taskService.failTask(1L);

        assertEquals(TaskStatus.FAILED, failedTask.getStatus());

        verify(taskJpaRepository).updateStatusIfCurrent(
                1L, "NEW", "IN_PROGRESS"
        );

        verify(taskJpaRepository).updateStatusIfCurrent(
                1L, "IN_PROGRESS", "FAILED"
        );
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {
        when(taskJpaRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertTrue(taskService.findById(999L).isEmpty());
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        when(taskJpaRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> taskService.startTask(999L)
        );
    }

    @Test
    void shouldNotCreateDuplicateTask() {
        when(taskJpaRepository.insertIfAbsent(
                1L, "Test", "LOW", "NEW"
        )).thenReturn(1, 0);

        taskService.createTask(1L, "Test", TaskPriority.LOW);

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(
                        1L, "Test", TaskPriority.LOW
                )
        );

        verify(taskJpaRepository, times(2)).insertIfAbsent(
                1L, "Test", "LOW", "NEW"
        );
    }
}