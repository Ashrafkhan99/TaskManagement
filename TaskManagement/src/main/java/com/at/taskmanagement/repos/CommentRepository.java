package com.at.taskmanagement.repos;

import com.at.taskmanagement.entity.Comment;
import com.at.taskmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTask(Task task);
    List<Comment> findByTaskId(UUID taskId);
}

