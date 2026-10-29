package com.example.tasklab.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO tasks (id, title, priority, status)
            VALUES (:id, :title, :priority, :status)
            ON CONFLICT (id) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("id") Long id,
            @Param("title") String title,
            @Param("priority") String priority,
            @Param("status") String status
    );

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tasks WHERE id = :id", nativeQuery = true)
    int deleteIfExists(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE tasks
            SET status = :newStatus
            WHERE id = :id AND status = :expectedStatus
            """, nativeQuery = true)
    int updateStatusIfCurrent(
            @Param("id") Long id,
            @Param("expectedStatus") String expectedStatus,
            @Param("newStatus") String newStatus
    );
}