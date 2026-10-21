package com.example.tasklab.service;

import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;
import com.example.tasklab.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceMockitoTest {

    private TaskRepository repository;
    private TaskService taskService;


    @BeforeEach
    void setUp() {
        repository = mock(TaskRepository.class);
        taskService = new TaskService(repository);
    }

    @Test
    void shouldStartTaskWithMockRepository() {

        Task task = new Task(1, "Test", TaskPriority.MEDIUM);
        when(repository.findById(1)).thenReturn(Optional.of(task));
        taskService.startTask(1);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        verify(repository).findById(1);

    }

    @Test
    void shouldThrowWhenTaskNotFoundWithMock() {

        when(repository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> taskService.startTask(1));
        verify(repository).findById(1);

    }
}


