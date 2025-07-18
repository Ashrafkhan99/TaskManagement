package com.at.taskmanagement.controller;

import com.at.taskmanagement.entity.Comment;
import com.at.taskmanagement.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestParam UUID taskId,
                                              @RequestParam String content,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.addComment(taskId, content, userDetails));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable UUID taskId,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId, userDetails));
    }
}
