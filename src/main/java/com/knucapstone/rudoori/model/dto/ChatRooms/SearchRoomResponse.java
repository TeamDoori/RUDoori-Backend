package com.knucapstone.rudoori.model.dto.ChatRooms;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRoomResponse {

    private String roomName;
    private String introduce;
    private String createdDt;

    //private List<Reply> replies = new ArrayList<>();
}
