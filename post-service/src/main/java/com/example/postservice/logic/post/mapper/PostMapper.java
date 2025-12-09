package com.example.postservice.logic.post.mapper;

import com.example.postservice.logic.post.entity.PostEntity;
import com.example.postservice.logic.post.model.dto.Attachment;
import com.example.postservice.logic.post.model.dto.ExtraData;
import com.example.postservice.logic.post.model.request.CreatePostRequest;
import com.example.postservice.logic.post.model.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private static final Random RANDOM = new Random();


    private static ExtraData generateRandomExtraData() {
        return new ExtraData(RANDOM.nextInt(1000), "RandomValue" + RANDOM.nextInt(1000));
    }


    private static List<ExtraData> generateRandomExtraDataList(int count) {
        List<ExtraData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(generateRandomExtraData());
        }
        return list;
    }


    public static PostEntity toEntity(CreatePostRequest r) {
        PostEntity e = new PostEntity();
        e.setUserId(r.userId());
        e.setTitle(r.title());
        e.setContent(r.content());
        e.setAttachment(r.attachment() != null ? r.attachment() :
                new Attachment("file.txt", "text/plain", 1024, "http://example.com/file.txt"));
        e.setExtraData(r.extraData() != null ? r.extraData() : generateRandomExtraDataList(3));
        return e;
    }


    public static PostResponse toResponse(PostEntity e) {
        return new PostResponse(
                e.getId(),
                e.getUserId(),
                e.getTitle(),
                e.getContent(),
                e.getAttachment(),
                e.getExtraData()
        );
    }

}
