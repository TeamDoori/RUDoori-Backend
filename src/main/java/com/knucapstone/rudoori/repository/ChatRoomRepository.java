package com.knucapstone.rudoori.repository;

import com.knucapstone.rudoori.model.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ChatRoomRepository extends MongoRepository<ChatRoom, String>, ChatRoomRepositoryCustom {

    ChatRoom findAllBy_id(String roomId);
}
