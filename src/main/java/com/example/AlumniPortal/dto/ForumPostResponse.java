package com.example.AlumniPortal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ForumPostResponse {

    private Long id;
    private String title;
    private String content;
    private String category;
    private String authorName;
    private int likesCount;
    private LocalDateTime createdAt;
    private List<String> tags;
    private List<CommentResponse> comments;
}