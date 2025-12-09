package com.example.postservice.logic.post.model.dto;

public record Attachment(
        String fileName,
        String fileType,
        long fileSize,
        String url
) {}
