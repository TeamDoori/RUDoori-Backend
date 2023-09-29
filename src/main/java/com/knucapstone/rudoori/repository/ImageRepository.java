package com.knucapstone.rudoori.repository;

import com.knucapstone.rudoori.model.entity.Image;
import com.knucapstone.rudoori.model.entity.Posts;
import com.knucapstone.rudoori.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByPost(Posts post);
    Optional<Image> findByUserId(String userId);
}