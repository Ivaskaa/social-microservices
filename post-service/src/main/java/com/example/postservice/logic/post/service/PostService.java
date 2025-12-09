package com.example.postservice.logic.post.service;

import com.example.postservice.logic.post.entity.PostEntity;
import com.example.postservice.logic.post.mapper.PostMapper;
import com.example.postservice.logic.post.model.request.CreatePostRequest;
import com.example.postservice.logic.post.model.response.PostResponse;
import com.example.postservice.logic.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;

    public PostResponse createPost(CreatePostRequest request) {
        PostEntity entity = postMapper.toEntity(request);
        entity = postRepository.save(entity);
        return postMapper.toResponse(entity);
    }

    public List<PostResponse> getPostsByUser(String userId) {
        return postRepository.findByUserId(userId).stream()
                .map(postMapper::toResponse)
                .toList();
    }
}
