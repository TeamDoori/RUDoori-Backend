package com.knucapstone.rudoori.model.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long scoreId;

    @Column(name = "opponent_id")
    private String opponentId;
    private double grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo userId;

    @Column(length = 100)
    private String roomName;

    @Column(length = 100)
    private String mention;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDt;

}
