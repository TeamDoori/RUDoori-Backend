package com.knucapstone.rudoori.model.dto.ChatRooms;

import com.google.gson.annotations.SerializedName;
import com.knucapstone.rudoori.model.entity.ChatMessage;
import lombok.Getter;
import lombok.Setter;

public class Chat {

    @Getter
    public static class Room {
        private String roomId;
        private String name;
    }
    @Getter
    @Setter
    public class Message {
        @SerializedName("type")
        private String type;

        @SerializedName("message")
        private MessageContent messageContent;

        @SerializedName("roomId")
        private String roomId;

        @Getter
        @Setter
        public static class MessageContent {

            @SerializedName("_id")
            private String messageId;

            @SerializedName("text")
            private String text;

            @SerializedName("createdAt")
            private String createdAt;

            @SerializedName("user")
            private User user;

        }

        // User 클래스는 user 객체 내의 정보를 나타내는 클래스입니다.
        @Getter
        @Setter
        public static class User {
            @SerializedName("_id")
            private String userId;

            @SerializedName("name")
            private String name;

            @SerializedName("avatar")
            private String avatarUrl;

        }

    }


}
