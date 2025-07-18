package com.at.taskmanagement.repos;

import com.at.taskmanagement.entity.Task;
import com.at.taskmanagement.entity.TaskStatus;
import com.at.taskmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByAssignedTo(User user);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    // Get all tasks assigned to or created by user
    List<Task> findByAssignedToIdOrCreatedById(UUID assignedId, UUID creatorId);

    // Search by title or description
    List<Task> findByCreatedByIdAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(UUID userId, String title, String desc);

    // Filter by status
    List<Task> findByCreatedByIdAndStatus(UUID userId, TaskStatus status);
}
