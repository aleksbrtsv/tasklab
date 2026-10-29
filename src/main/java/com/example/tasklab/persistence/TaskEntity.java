package com.example.tasklab.persistence;

import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    protected TaskEntity() {
    }

    public TaskEntity(Long id, String title, TaskPriority priority, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
    }
}
