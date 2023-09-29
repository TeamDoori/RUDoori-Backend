package com.knucapstone.rudoori.model.dto.ChatRooms;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class ChatSystem {
        @Getter
        @Setter
        public class Message {
            @SerializedName("type")
            private String type;

            @SerializedName("message")
            private MessageContent messageContent;

            @SerializedName("roomId")
            private String roomId;
            @SerializedName("command")
            private String command;

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
                private String system;

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
