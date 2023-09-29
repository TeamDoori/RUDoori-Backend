package com.knucapstone.rudoori.model.dto.ChatRooms;

import com.knucapstone.rudoori.model.entity.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoomResponse {

    private String _id;
    private String roomName;
    private ChatRoom.User hostUser;
    private String introduce;
    private Set<ChatRoom.User> participants;
    private int maxParticipants;
    private Set<String> blockedMember;
    private LocalDateTime createdAt;
    private String category;
    private boolean isFull;
    private boolean isUsed;
    private boolean isCompleted;
    private Integer completeCounter;

}
