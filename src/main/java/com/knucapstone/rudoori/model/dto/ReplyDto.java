package com.knucapstone.rudoori.model.dto;

import com.knucapstone.rudoori.model.entity.Reply;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyDto {

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReplyRequest {

        String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreateReplyResponse {

        Long replyId;
        String nickname;
        String userId;
        String content;
        List<Reply> children;
    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreateChildrenReplyResponse {

        Long replyId;
        Long parentReplyId;
        String nickname;
        String userId;
        String content;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyGroup {

        Long replyId;
        String nickname;
        String content;

        String userId;

        List<ReplyDto.CreateChildrenReplyResponse> children;
    }


}
