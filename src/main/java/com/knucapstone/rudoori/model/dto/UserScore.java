package com.knucapstone.rudoori.model.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


public class UserScore {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserScoreResponse {
        private String score;
        private double point;
        private List<Mention> mentions;

//        private List<OpponentMention> opponentsMention;
    }
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Mention {
        private Long mentionId;
        private String mentionUserId; // 댓글 남긴사람
        private String mentionUserNickname;
        private String content;
        private String roomName;
        private LocalDateTime createdDt;
        private Double score;
    }

}
