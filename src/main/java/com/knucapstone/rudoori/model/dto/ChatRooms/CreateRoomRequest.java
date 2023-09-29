package com.knucapstone.rudoori.model.dto.ChatRooms;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoomRequest {

    String hostId;
    String roomName;
    int numbers;
    String introduce;
    String category;

}
