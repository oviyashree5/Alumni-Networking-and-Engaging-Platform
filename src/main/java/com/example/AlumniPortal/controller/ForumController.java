package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.CommentRequest;
import com.example.AlumniPortal.dto.CommentResponse;
import com.example.AlumniPortal.dto.ForumPostRequest;
import com.example.AlumniPortal.dto.ForumPostResponse;
import com.example.AlumniPortal.security.CustomUserDetails;
import com.example.AlumniPortal.service.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@CrossOrigin
public class ForumController {

    private final ForumService forumService;

    @PostMapping
    @PreAuthorize("hasRole('ALUMNI')")
    public ForumPostResponse createPost(
            @Valid @RequestBody ForumPostRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        return forumService.createPost(request, user);
    }

    @GetMapping
    public List<ForumPostResponse> getAllPosts() {
        return forumService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ForumPostResponse getPost(@PathVariable Long id) {
        return forumService.getPostById(id);
    }

    @PutMapping("/{id}/like")
    public String likePost(@PathVariable Long id) {
        forumService.likePost(id);
        return "Post liked successfully";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePost(@PathVariable Long id) {
        forumService.deletePost(id);
        return "Post deleted successfully";
    }

    @PostMapping("/{postId}/comment")
    @PreAuthorize("hasRole('ALUMNI')")
    public CommentResponse addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        return forumService.addComment(postId, request, user);
    }

    @GetMapping("/category/{category}")
    public List<ForumPostResponse> getByCategory(@PathVariable String category) {
        return forumService.getByCategory(category);
    }
}
