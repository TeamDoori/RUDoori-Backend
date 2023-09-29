package com.knucapstone.rudoori.model.dto.Board;


import com.knucapstone.rudoori.model.dto.ReplyDto;
import com.knucapstone.rudoori.model.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {

    private Long postId;
    private String userId;
    private String title;
    private String content;
    private String writer;
    private int scrap;
    private LocalDateTime createdDt;
    // 해당 게시글의 댓글 모음
    private List<ReplyDto.ReplyGroup> replyGroup;
    private List<Image> imageList;

//    private String media;


    public BoardResponse(Long postId, String title, String content, String writer, int scrap, LocalDateTime createdDt, List<Image> imageList,String userId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.scrap = scrap;
        this.createdDt = createdDt;
        this.imageList = imageList;
        this.userId = userId;

    }
    public BoardResponse(Long postId, String title, String content, String writer, int scrap, LocalDateTime createdDt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.scrap = scrap;
        this.createdDt = createdDt;
    }


    public BoardResponse(Long postId, String title, String content) {
        this.postId = postId;
        this.title = title;
        this.content = content;
    }

}