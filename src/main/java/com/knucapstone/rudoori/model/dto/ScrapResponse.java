package com.knucapstone.rudoori.model.dto;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScrapResponse {

    private Long postId;
    private String userId;

    private String title;
    private String content;
    private String writer;

    private LocalDateTime createdDt;

}
