package com.knucapstone.rudoori.model.dto.ChatRooms;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlockUser {
    private String type;
    private String roomId;
    private List<String> message;
}
