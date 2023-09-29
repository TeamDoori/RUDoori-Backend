package com.knucapstone.rudoori.repository;

import com.knucapstone.rudoori.model.entity.Posts;
import com.knucapstone.rudoori.model.entity.Reply;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository  extends JpaRepository<Reply, Long> {

    Optional<Reply> findById(@NotNull Long commentId);

    List<Reply> findAllByPost(Optional<Posts> post);

    void deleteByReplyId(Long commentId);
}
