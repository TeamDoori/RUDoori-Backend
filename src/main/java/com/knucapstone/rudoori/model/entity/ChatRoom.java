package com.knucapstone.rudoori.model.entity;

import jakarta.persistence.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatRoom")
public class ChatRoom {
    @Id
    private String _id;
    private String roomName;    //방 이름
    private String introduce;   //방 소개
    private User hostUser;
    private LocalDateTime createdAt;    //생성 시간
    private Set<User> participants;  //현재 참가자
    private int maxParticipants;        //목표 참가자
    private Set<String> blockedMember; //
    private boolean isFull;
    private boolean isUsed;
    private boolean isComplete;
    private String category;
    private Integer completeCounter;

    @Getter
    @Setter
    public static class User {
        private String _id;
        private String name;
        private String avatar;
    }
}
