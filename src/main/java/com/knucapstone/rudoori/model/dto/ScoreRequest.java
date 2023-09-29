package com.knucapstone.rudoori.model.dto;

import com.knucapstone.rudoori.model.entity.Score;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoreRequest {

    private String roomName;
    private List<Map<String, String>> scoreList;
}
