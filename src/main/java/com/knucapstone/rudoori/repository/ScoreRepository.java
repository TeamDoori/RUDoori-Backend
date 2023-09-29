package com.knucapstone.rudoori.repository;


import com.knucapstone.rudoori.model.entity.Score;
import com.knucapstone.rudoori.model.entity.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByOpponentId(String opponentId);

    long countByOpponentId(String columnId);
    @Query("SELECT count(s.grade) FROM Score s WHERE s.userId.userId = :userId")
    Optional<Long> countByUserId(@Param("userId") String userId);

    @Query("SELECT SUM(s.grade) FROM Score s WHERE s.userId.userId = :userId")
    Optional<Double> sumOfGradesByUserId(@Param("userId") String userId);

    Optional<List<Score>> findByUserId(String userId, Pageable pageable);

//    List<Score> findTop10ByUserIdOrderByCreatedDtDesc(UserInfo userId);

    List<Score> findTop5ByUserIdOrderByCreatedDtDesc(UserInfo userInfo);
}
