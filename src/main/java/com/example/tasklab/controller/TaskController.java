package com.example.tasklab.controller;

import com.example.tasklab.dto.CreateTaskRequest;
import com.example.tasklab.dto.TaskApiMapper;
import com.example.tasklab.dto.TaskResponse;
import com.example.tasklab.model.Task;
import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;
import com.example.tasklab.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskApiMapper taskApiMapper;

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable long id) {
        return ResponseEntity.of(taskService.findById(id).map(taskApiMapper::toResponse));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(request.id(), request.title(), request.priority());
        return ResponseEntity.status(HttpStatus.CREATED).body(taskApiMapper.toResponse(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Page<TaskResponse> getTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return taskService.searchTasks(title, status, priority, pageable).map(taskApiMapper::toResponse);
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<TaskResponse> startTask(@PathVariable long id) {
        Task task = taskService.startTask(id);
        return ResponseEntity.ok(taskApiMapper.toResponse(task));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable long id) {
        Task task = taskService.completeTask(id);
        return ResponseEntity.ok(taskApiMapper.toResponse(task));
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<TaskResponse> failTask(@PathVariable long id) {
        Task task = taskService.failTask(id);
        return ResponseEntity.ok(taskApiMapper.toResponse(task));
    }
}
