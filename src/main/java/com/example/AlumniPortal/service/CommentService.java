package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.CommentRequest;
import com.example.AlumniPortal.dto.CommentResponse;
import com.example.AlumniPortal.entity.Comment;
import com.example.AlumniPortal.entity.ForumPost;
import com.example.AlumniPortal.entity.User;
import com.example.AlumniPortal.exception.BadRequestException;
import com.example.AlumniPortal.exception.ResourceNotFoundException;
import com.example.AlumniPortal.repository.CommentRepository;
import com.example.AlumniPortal.repository.ForumPostRepository;
import com.example.AlumniPortal.repository.UserRepository;
import com.example.AlumniPortal.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ForumPostRepository forumPostRepository;
    private final UserRepository userRepository;

    public CommentResponse createComment(CommentRequest request, CustomUserDetails user) {
        if (request.getPostId() == null) {
            throw new BadRequestException("Post id is required");
        }

        ForumPost post = forumPostRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = Comment.builder()
                .postId(post.getId())
                .userId(user.getUserId())
                .content(request.getContent().trim())
                .build();

        return mapToResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> getCommentsForPost(Long postId) {
        if (!forumPostRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found");
        }

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteComment(Long id, CustomUserDetails user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isAdmin && !comment.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You cannot delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        User author = userRepository.findById(comment.getUserId()).orElse(null);

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .authorName(author != null ? author.getEmail() : "Unknown user")
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
