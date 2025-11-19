package com.people10.dashboard.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentResponseDto {
    private String commentedBy;
    private String comment;
    private LocalDateTime createdAt;
}
