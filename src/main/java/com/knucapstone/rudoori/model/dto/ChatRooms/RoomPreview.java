package com.knucapstone.rudoori.model.dto.ChatRooms;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoomPreview {

    private String _id;
    private String roomName;
    private String introduce;
    private int maxParticipants;
    private LocalDateTime createdAt;
}