package com.knucapstone.rudoori.model.dto.ChatRooms;

import com.knucapstone.rudoori.model.entity.ChatMessage;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageResponse {

    private String _id;
    private String text;
    private String createdAt;
    private User user;

    // 생성자, getter, setter 등의 필요한 메서드들을 추가로 구현할 수 있습니다.

    // User 클래스는 다음과 같이 정의할 수 있습니다.
    @Getter
    @Setter
    public static class User{
        private String _id;
        private String name;
        private String avatar;

        public User(String id, String name, String avatar) {
            this._id = id;
            this.name = name;
            this.avatar = avatar;
        }
    }

}
