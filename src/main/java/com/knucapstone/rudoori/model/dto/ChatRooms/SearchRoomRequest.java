package com.knucapstone.rudoori.model.dto.ChatRooms;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchRoomRequest {

    private String[] keywordList;
    private String category;
}
