package com.people10.dashboard.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentDto {
    private Long userId;
    private String comment;
    private LocalDateTime createdAt;
}
