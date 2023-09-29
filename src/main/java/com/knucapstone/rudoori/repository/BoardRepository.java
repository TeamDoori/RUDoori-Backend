package com.knucapstone.rudoori.repository;

import com.knucapstone.rudoori.model.entity.Posts;
import com.knucapstone.rudoori.model.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BoardRepository extends JpaRepository<Posts, Long> {
    Optional<Posts> findById(Long postId);
    @Query("SELECT MAX(p.postId) FROM Posts p")
    Long findMaxPostId();

    Page<Posts> findByPostIdLessThanOrderByPostIdDesc(Long postId, PageRequest pageRequest) ;
    Page<Posts> findAllByUserId(UserInfo userId, Pageable pageable);
    Page<Posts> findAllByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
   @Query("SELECT s.postId FROM UserScraps s WHERE s.userId.userId = :userId")
    List<Posts> findUserScrapsList(@Param("userId") String userId);

}
