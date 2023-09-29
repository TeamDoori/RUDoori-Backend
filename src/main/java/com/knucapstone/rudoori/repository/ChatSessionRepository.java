package com.knucapstone.rudoori.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor

public class ChatSessionRepository {
    // Redis CacheKeys
    /**
     * opsForValue() : Redis의 String타입을 Serialize / Deserialize 해주는 인터페이스
     * opsForList() : Redis의 List타입을 Serialize / Deserialize 해주는 인터페이스
     * opsForSet() : Redis의 Set타입을 Serialize / Deserialize 해주는 인터페이스
     * opsForZset() : Redis의 Zset타입을 Serialize / Deserialize 해주는 인터페이스
     * opsForHash() : Redis의 Hash타입을 Serialize / Deserialize 해주는 인터페이스
     */
    @Autowired
    private final RedisTemplate<String, String> redisTemplate;

    public void createChat(String roomId, WebSocketSession session) {
        redisTemplate.opsForSet().add(roomId, session.getId());
    }


    public Set<String> getWebSocketSessionsByRoomId(String roomId) {
        // roomId를 key로 하여 해당 방의 모든 WebSocketSession을 가져옴
        return redisTemplate.opsForSet().members(roomId);
    }

    public void removeWebSocketSession(String roomId, WebSocketSession session) {
        // roomId를 key로 하여 해당 방의 WebSocketSession 중에서 session과 일치하는 것을 제거
        redisTemplate.opsForSet().remove(roomId,session.getId());
    }

    public void deleteChatRoom(String roomId){
        redisTemplate.opsForSet().remove(roomId);
    }
}
