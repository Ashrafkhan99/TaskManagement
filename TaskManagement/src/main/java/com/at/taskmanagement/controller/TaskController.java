package com.at.taskmanagement.controller;

import com.at.taskmanagement.entity.Task;
import com.at.taskmanagement.entity.TaskStatus;
import com.at.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.createTask(title, description, dueDate, userDetails));
    }

    @PutMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<Task> assignTask(
            @PathVariable UUID taskId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.assignTask(taskId, userId, userDetails));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<Task>> getMyTasks(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getMyTasks(userDetails));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> search(@RequestParam String query) {
        return ResponseEntity.ok(taskService.searchTasks(query));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Task>> filter(@RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.filterByStatus(status));
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<Task> completeTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.markCompleted(taskId, userDetails));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable UUID taskId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.updateTask(taskId, title, description, dueDate, userDetails));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTask(taskId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String keyword,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.searchTasks(keyword, userDetails));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Task>> filterTasks(@RequestParam TaskStatus status,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.filterByStatus(status, userDetails));
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Task>> sortTasks(@RequestParam String field,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.sortByField(field, userDetails));
    }

}
