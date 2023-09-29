package com.knucapstone.rudoori.repository;

import com.knucapstone.rudoori.model.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query("{ 'chatRoomId': ?0 }")
    List<ChatMessage> findAllByChatRoomId(String chatRoomId);

}
