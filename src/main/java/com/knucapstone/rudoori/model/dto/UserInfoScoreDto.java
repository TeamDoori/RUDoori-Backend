package com.knucapstone.rudoori.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoScoreDto {

    private String major;
    private String nickname;
    private Double score;
    private String image;
    private double point;
    private List<UserScore.Mention> mentions;

    @Builder
    public UserInfoScoreDto(String major, String nickname, Double score, String image, double point, List<UserScore.Mention> mentions) {
        this.major = major;
        this.nickname = nickname;
        this.score = score;
        this.image = image;
        this.point = point;
        this.mentions = mentions;
    }

}
