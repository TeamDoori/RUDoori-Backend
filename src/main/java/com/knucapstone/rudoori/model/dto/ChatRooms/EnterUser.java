package com.knucapstone.rudoori.model.dto.ChatRooms;

import com.knucapstone.rudoori.model.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class EnterUser {
    private String type;
    private String roomId;
    private Set<ChatRoom.User> message = new HashSet<>();
}
