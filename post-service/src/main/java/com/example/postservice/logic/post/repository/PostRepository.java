package com.example.postservice.logic.post.repository;

import com.example.postservice.logic.post.entity.PostEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<PostEntity, String> {
    List<PostEntity> findByUserId(String userId);
}
