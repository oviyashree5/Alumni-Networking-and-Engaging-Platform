package com.example.AlumniPortal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    private Long postId;

    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment content must be at most 1000 characters")
    private String content;
}
