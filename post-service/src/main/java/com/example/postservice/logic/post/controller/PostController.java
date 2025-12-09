package com.example.postservice.logic.post.controller;

import com.example.postservice.logic.post.model.request.CreatePostRequest;
import com.example.postservice.logic.post.model.response.PostResponse;
import com.example.postservice.logic.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostResponse create(@RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    @GetMapping("/user/{userId}")
    public List<PostResponse> getByUser(@PathVariable String userId) {
        return postService.getPostsByUser(userId);
    }
}