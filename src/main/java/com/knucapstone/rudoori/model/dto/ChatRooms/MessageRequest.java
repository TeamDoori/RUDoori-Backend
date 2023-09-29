package com.knucapstone.rudoori.model.dto.ChatRooms;

import com.knucapstone.rudoori.model.entity.ChatMessage;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageRequest {

    private Object data;

}
