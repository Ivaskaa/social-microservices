package com.example.postservice.logic.post.service;

import com.example.postservice.logic.post.entity.PostEntity;
import com.example.postservice.logic.post.mapper.PostMapper;
import com.example.postservice.logic.post.model.request.CreatePostRequest;
import com.example.postservice.logic.post.model.response.PostResponse;
import com.example.postservice.logic.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponse createPost(CreatePostRequest request) {
        PostEntity entity = PostMapper.toEntity(request);
        entity = postRepository.save(entity);
        return PostMapper.toResponse(entity);
    }


    public List<PostResponse> getPostsByUser(String userId) {
        return postRepository.findByUserId(userId).stream()
                .map(PostMapper::toResponse)
                .toList();
    }
}
