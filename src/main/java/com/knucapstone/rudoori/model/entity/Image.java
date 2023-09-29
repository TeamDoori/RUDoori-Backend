package com.knucapstone.rudoori.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private String uploadFileName;
    private String storeFileName;
    private String path;
    private String roomId;
    private String userId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    Posts post;



    @Builder
    public Image(String uploadFileName, String storeFileName, String path, Posts post, String userId, String roomId) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.path = path;
        this.post = post;
        this.userId = userId;
        this.roomId = roomId;
    }
}