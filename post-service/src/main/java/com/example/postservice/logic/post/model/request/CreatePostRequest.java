package com.example.postservice.logic.post.model.request;

import com.example.postservice.logic.post.model.dto.Attachment;
import com.example.postservice.logic.post.model.dto.ExtraData;

import java.util.List;

public record CreatePostRequest(
        String userId,
        String title,
        String content,
        Attachment attachment,
        List<ExtraData> extraData
) {}
