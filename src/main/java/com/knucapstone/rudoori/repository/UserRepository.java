package com.knucapstone.rudoori.repository;

import com.knucapstone.rudoori.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo, String> {
    Optional<UserInfo> findByUserId(String userId);
    Optional<UserInfo> findByEmail(String email);

    Optional<List<UserInfo>> findByUserIdIn(List<String> opponentIds);
}
