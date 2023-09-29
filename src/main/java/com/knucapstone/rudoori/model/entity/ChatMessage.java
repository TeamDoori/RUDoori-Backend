package com.knucapstone.rudoori.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatMessage")
public class ChatMessage {
    @Id
    private String _id;
    private String chatRoomId;
    @CreatedDate
    private LocalDateTime createdAt;    //방 생성 시간
    @JsonIgnore
    private Message messageContent;                  //메시지

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Message {

        private String _id;
        private String text;
        private String createdAt;
        private User user;

        // 생성자, getter, setter 등의 필요한 메서드들을 추가로 구현할 수 있습니다.

        // User 클래스는 다음과 같이 정의할 수 있습니다.
        @Getter
        @Setter
        public static class User {
            private String _id;
            private String name;
            private String avatar;
        }

    }
}
