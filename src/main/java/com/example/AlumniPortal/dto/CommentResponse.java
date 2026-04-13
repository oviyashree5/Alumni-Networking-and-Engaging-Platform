package com.example.AlumniPortal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
}
