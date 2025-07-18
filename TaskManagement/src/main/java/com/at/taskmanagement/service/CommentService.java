package com.at.taskmanagement.service;

import com.at.taskmanagement.entity.*;
import com.at.taskmanagement.repos.CommentRepository;
import com.at.taskmanagement.repos.TaskRepository;
import com.at.taskmanagement.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Comment addComment(UUID taskId, String content, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Team check
        if (user.getTeam() == null || task.getCreatedBy().getTeam() == null ||
                !user.getTeam().getId().equals(task.getCreatedBy().getTeam().getId())) {
            throw new RuntimeException("You can only comment on tasks within your team");
        }

        Comment comment = Comment.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .user(user)
                .task(task)
                .build();

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsForTask(UUID taskId, UserDetails userDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTeam() == null || task.getCreatedBy().getTeam() == null ||
                !user.getTeam().getId().equals(task.getCreatedBy().getTeam().getId())) {
            throw new RuntimeException("You cannot view comments from a different team");
        }

        return commentRepository.findByTaskId(taskId);
    }
}
