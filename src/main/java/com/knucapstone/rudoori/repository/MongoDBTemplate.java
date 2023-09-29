package com.knucapstone.rudoori.repository;

import com.knucapstone.rudoori.model.entity.ChatMessage;
import com.knucapstone.rudoori.model.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MongoDBTemplate
{
    private final MongoTemplate mongoTemplate;
    private int pageSize = 30;


    public List<ChatRoom> getChatRoomsByUserId(String userId) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("chatRoom")
                .localField("chatRoomIds")
                .foreignField("_id")
                .as("chatRooms");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(userId)),
                lookupOperation,
                Aggregation.unwind("chatRooms"),
                Aggregation.replaceRoot("chatRooms")
        );

        AggregationResults<ChatRoom> results = mongoTemplate.aggregate(aggregation, "involveRoom", ChatRoom.class);

        return results.getMappedResults();
    }
    public List<ChatMessage> getChatMessagesByRoomId(String roomId, int page) {
        Query query = new Query(Criteria.where("chatRoomId").is(roomId));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.skip((page - 1) * pageSize);
        query.limit(pageSize);

        return mongoTemplate.find(query, ChatMessage.class);
    }

    // 총 페이지 수를 반환하는 메서드
    public int getTotalPages(String roomId) {
        Query query = new Query(Criteria.where("chatRoomId").is(roomId));
        long totalCount = mongoTemplate.count(query, ChatMessage.class);
        return (int) Math.ceil((double) totalCount / pageSize);
    }

}
