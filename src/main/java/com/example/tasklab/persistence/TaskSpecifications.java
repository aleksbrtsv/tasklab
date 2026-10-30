package com.example.tasklab.persistence;

import com.example.tasklab.model.TaskPriority;
import com.example.tasklab.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class TaskSpecifications {

    public static Specification<TaskEntity> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<TaskEntity> hasPriority(TaskPriority priority) {
        return (root, query, cb) ->
                cb.equal(root.get("priority"), priority);
    }

    public static Specification<TaskEntity> titleContains(String title) {

        String escapedTitle = title.toLowerCase(Locale.ROOT)
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");

        return (root, query, cb) ->
                cb.like(cb.lower(
                        root.get("title")),
                        "%" + escapedTitle + "%",
                        '!'
                );
    }
}
