package com.knucapstone.rudoori.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {
    
    // media 관련 추가해야 함

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    private String title;
    private String content;
    private String writer;
    private int likeCount;
    private int dislikeCount;
    private int scrap;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDt;

    @UpdateTimestamp
    private LocalDateTime modifiedDt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo userId;

    @JsonManagedReference
    @OneToMany(mappedBy = "post")
    private List<Image> images = new ArrayList<>();


    @OneToMany(mappedBy = "postId", orphanRemoval = true)
    private List<UserScraps> scraps = new ArrayList<>();


    @OneToMany(mappedBy = "post", orphanRemoval = true)   //orphanRemoval : 게시글 삭제시, 댓글 자동삭제
    private List<Reply> replies = new ArrayList<>();

    public Posts(Long postId, String title, String content, String writer, int likeCount, int dislikeCount, int scrap, LocalDateTime createdDt, LocalDateTime modifiedDt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.scrap = scrap;
        this.createdDt = createdDt;
        this.modifiedDt = modifiedDt;
    }
}
