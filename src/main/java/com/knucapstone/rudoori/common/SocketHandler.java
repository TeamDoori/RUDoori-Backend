package com.knucapstone.rudoori.common;

import com.google.gson.Gson;
import com.knucapstone.rudoori.model.dto.ChatRooms.BlockUser;
import com.knucapstone.rudoori.model.dto.ChatRooms.Chat;
import com.knucapstone.rudoori.model.dto.ChatRooms.ChatSystem;
import com.knucapstone.rudoori.model.dto.ChatRooms.EnterUser;
import com.knucapstone.rudoori.repository.ChatSessionRepository;
import com.knucapstone.rudoori.service.ChatRoomService;
import com.knucapstone.rudoori.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final ChatSessionRepository chatSessionRepository;
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap();
    private final ChatRoomService service;
    private final Gson gson = new Gson();

    public ConcurrentHashMap<String, WebSocketSession> getSessions(){
        return sessions;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = session.getAttributes().get("room").toString();
        chatSessionRepository.createChat(roomId, session);
        sessions.put(session.getId(), session);
        log.info(roomId);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        chatSessionRepository.removeWebSocketSession(session.getAttributes().get("room").toString(), session);
        sessions.remove(session.getId());
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info(payload);
        String TYPE = payload.substring(9, 11);

        switch (TYPE) {
            case "TA" -> {
                Chat.Message data = gson.fromJson(payload, Chat.Message.class);
                log.info(data.getType());
                Set<String> sessionId = chatSessionRepository.getWebSocketSessionsByRoomId(data.getRoomId());
                service.saveSendMessage(data.getMessageContent(), data.getRoomId());
                for (String s : sessionId) {
                    WebSocketSession savedSession = sessions.get(s);
                    if (savedSession != null && savedSession.isOpen()) {
                        savedSession.sendMessage(new TextMessage(gson.toJson(data.getMessageContent())));
                    } else {
                        if (savedSession != null) {
                            chatSessionRepository.removeWebSocketSession(data.getRoomId(), savedSession);
                        }
                        sessions.remove(s);
                    }
                }
            }
            case "EN" -> {
                Gson gson = new Gson();
                ChatSystem.Message data2 = gson.fromJson(payload, ChatSystem.Message.class);
//                data2.getMessageContent().setText(gson.toJson(service.enterRoom(data2.getRoomId())));
                EnterUser enterUser = EnterUser.builder()
                        .type(data2.getType())
                        .roomId(data2.getRoomId())
                        .message(service.enterRoom(data2.getRoomId()))
                        .build();
                Set<String> sessionId = chatSessionRepository.getWebSocketSessionsByRoomId(data2.getRoomId());
                for (String s : sessionId) {
                    WebSocketSession savedSession = sessions.get(s);
                    if (savedSession != null && savedSession.isOpen()) {
                        savedSession.sendMessage(new TextMessage(gson.toJson(enterUser)));
                    } else {
                        if (savedSession != null) {
                            chatSessionRepository.removeWebSocketSession(data2.getRoomId(), savedSession);
                        }
                        sessions.remove(s);
                    }
                }
                log.info(data2.getType());
//                for (WebSocketSession s : sessions) {
//                    s.sendMessage(new TextMessage(payload));
//                }
            }
            case "BL" -> {
                Gson gson = new Gson();
                BlockUser blockUser = gson.fromJson(payload, BlockUser.class);
                service.blockUser(blockUser);
                Set<String> sessionId = chatSessionRepository.getWebSocketSessionsByRoomId(blockUser.getRoomId());
                for (String s : sessionId) {
                    WebSocketSession savedSession = sessions.get(s);
                    if (savedSession != null && savedSession.isOpen()) {
                        savedSession.sendMessage(new TextMessage(gson.toJson(blockUser)));
                    } else {
                        if (savedSession != null) {
                            chatSessionRepository.removeWebSocketSession(blockUser.getRoomId(), savedSession);
                        }
                        sessions.remove(s);
                    }
                }

            }
            case "FI" -> {
                Gson gson = new Gson();
                ChatSystem.Message exitMessage = gson.fromJson(payload, ChatSystem.Message.class);
                Set<String> sessionId = chatSessionRepository.getWebSocketSessionsByRoomId(exitMessage.getRoomId());
                for (String s : sessionId) {
                    WebSocketSession savedSession = sessions.get(s);
                    if (savedSession != null && savedSession.isOpen()) {
                        savedSession.sendMessage(new TextMessage(gson.toJson(exitMessage)));
                        sessions.remove(s);
                    } else {
                        if (savedSession != null) {
                            chatSessionRepository.removeWebSocketSession(exitMessage.getRoomId(), savedSession);
                        }
                        sessions.remove(s);
                    }
                }

            }
            default -> System.out.println(TYPE);
        }

    }
}
