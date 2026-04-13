package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.CommentRequest;
import com.example.AlumniPortal.dto.CommentResponse;
import com.example.AlumniPortal.dto.ForumPostRequest;
import com.example.AlumniPortal.dto.ForumPostResponse;
import com.example.AlumniPortal.entity.ForumPost;
import com.example.AlumniPortal.exception.ResourceNotFoundException;
import com.example.AlumniPortal.repository.ForumPostRepository;
import com.example.AlumniPortal.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumPostRepository postRepository;
    private final CommentService commentService;

    @Transactional
    public ForumPostResponse createPost(ForumPostRequest request,
                                        CustomUserDetails user) {

        ForumPost post = ForumPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .authorId(user.getUserId())
                .authorName(user.getUsername())
                .tags(request.getTags())
                .build();

        return mapToResponse(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public List<ForumPostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ForumPostResponse getPostById(Long id) {
        ForumPost post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return mapToResponse(post);
    }

    public void likePost(Long id) {
        ForumPost post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public CommentResponse addComment(Long postId,
                                      CommentRequest request,
                                      CustomUserDetails user) {
        request.setPostId(postId);
        return commentService.createComment(request, user);
    }

    @Transactional(readOnly = true)
    public List<ForumPostResponse> getByCategory(String category) {
        return postRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ForumPostResponse mapToResponse(ForumPost post) {
        List<String> tags = post.getTags() == null ? List.of() : List.copyOf(post.getTags());
        return ForumPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .authorName(post.getAuthorName())
                .likesCount(post.getLikesCount())
                .createdAt(post.getCreatedAt())
                .tags(tags)
                .comments(commentService.getCommentsForPost(post.getId()))
                .build();
    }
}
