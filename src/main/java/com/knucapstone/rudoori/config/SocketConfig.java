package com.knucapstone.rudoori.config;

import com.knucapstone.rudoori.common.SocketHandler;
import com.knucapstone.rudoori.repository.ChatSessionRepository;
import com.knucapstone.rudoori.service.ChatRoomService;
import com.knucapstone.rudoori.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {
    private final ChatSessionRepository repository;
    private final ChatRoomService service;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(repository, service), "/ws/chat").addInterceptors(new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//                String id = request.getHeaders().get("_id").toString();
                String room = request.getHeaders().getFirst("room");
                String user = request.getHeaders().getFirst("user");
                attributes.put("room", room);
                attributes.put("user", user);
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        }).setAllowedOrigins("*");
    }
}
