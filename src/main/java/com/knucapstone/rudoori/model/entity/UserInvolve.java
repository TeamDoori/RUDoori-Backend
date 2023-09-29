package com.knucapstone.rudoori.model.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "involveRoom")
public class UserInvolve {
    @Id
    private String id;
    private Set<String> chatRoomIds = new HashSet<>();

}
