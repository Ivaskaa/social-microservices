package com.example.postservice.logic.post.entity;

import com.example.postservice.logic.post.model.dto.Attachment;
import com.example.postservice.logic.post.model.dto.ExtraData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document("posts")
public class PostEntity {

    @Id
    private String id;
    private String userId;
    private String title;
    private String content;
    private Attachment attachment;
    private List<ExtraData> extraData;

}
