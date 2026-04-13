package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.CommentRequest;
import com.example.AlumniPortal.dto.CommentResponse;
import com.example.AlumniPortal.security.CustomUserDetails;
import com.example.AlumniPortal.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('ALUMNI')")
    public CommentResponse createComment(
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return commentService.createComment(request, user);
    }

    @GetMapping("/post/{postId}")
    public List<CommentResponse> getCommentsForPost(@PathVariable Long postId) {
        return commentService.getCommentsForPost(postId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ALUMNI')")
    public void deleteComment(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails user) {
        commentService.deleteComment(id, user);
    }
}
