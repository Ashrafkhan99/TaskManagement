package com.at.taskmanagement.service;

import com.at.taskmanagement.entity.Task;
import com.at.taskmanagement.entity.TaskStatus;
import com.at.taskmanagement.entity.User;
import com.at.taskmanagement.repos.TaskRepository;
import com.at.taskmanagement.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Task createTask(String title, String description, LocalDate dueDate, UserDetails userDetails) {
        User creator = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (creator.getTeam() == null) {
            throw new RuntimeException("User must be part of a team to create a task");
        }

        Task task = Task.builder()
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .status(TaskStatus.OPEN)
                .createdBy(creator)
                .build();

        return taskRepository.save(task);
    }


    public Task assignTask(UUID taskId, UUID assigneeId, UserDetails userDetails) {
        User requester = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getCreatedBy().getId().equals(requester.getId())) {
            throw new RuntimeException("Only the creator can assign the task");
        }

        if (requester.getTeam() == null) {
            throw new RuntimeException("You must be part of a team to assign tasks");
        }

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));

        if (assignee.getTeam() == null || !assignee.getTeam().getId().equals(requester.getTeam().getId())) {
            throw new RuntimeException("Assignee must be in the same team");
        }

        task.setAssignedTo(assignee);
        return taskRepository.save(task);
    }


    public List<Task> getMyTasks(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByAssignedTo(user);
    }

    public List<Task> searchTasks(String query) {
        return taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    public List<Task> filterByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public Task markCompleted(UUID taskId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!Objects.equals(task.getAssignedTo().getId(), user.getId()))
            throw new RuntimeException("Only assignee can mark as complete");

        task.setStatus(TaskStatus.COMPLETED);
        return taskRepository.save(task);
    }

    public Task updateTask(UUID taskId, String title, String description, LocalDate dueDate, UserDetails userDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getCreatedBy().getUsername().equals(userDetails.getUsername()))
            throw new RuntimeException("Only creator can update");

        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    public void deleteTask(UUID taskId, UserDetails userDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getCreatedBy().getUsername().equals(userDetails.getUsername()))
            throw new RuntimeException("Only creator can delete");

        taskRepository.delete(task);
    }

    public List<Task> searchTasks(String keyword, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByCreatedByIdAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                user.getId(), keyword, keyword
        );
    }

    public List<Task> filterByStatus(TaskStatus status, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByCreatedByIdAndStatus(user.getId(), status);
    }

    public List<Task> sortByField(String field, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Task> tasks = taskRepository.findByAssignedToIdOrCreatedById(user.getId(), user.getId());

        return switch (field) {
            case "dueDate" -> tasks.stream().sorted(Comparator.comparing(Task::getDueDate)).toList();
            case "title" -> tasks.stream().sorted(Comparator.comparing(Task::getTitle)).toList();
            default -> tasks;
        };
    }

}
