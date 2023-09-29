//package com.knucapstone.rudoori.repository;
//
//import com.knucapstone.rudoori.model.dto.UserScore;
//import com.knucapstone.rudoori.model.entity.Mention;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import com.knucapstone.rudoori.model.dto.UserScore.OpponentMention;
//
//// ...
//
//import java.util.List;
//
//public interface MentionRepository extends JpaRepository<Mention, Long> {
//        List<Mention> findAllByOpponentId(String opponentId);
////        @Query("SELECT new com.knucapstone.rudoori.model.dto.UserScore$OpponentMention(m.mentionId,  m.userId as mentionUserId, u.nickname, m.content) " +
////                "FROM Mention m INNER JOIN m.userId u " +
////                "WHERE m.opponentId = :opponentId") // m의 opponent 는 멘션 받은사람, 파라미터로 받은건 멘션받은사람
////        List<UserScore.OpponentMention> findOpponentMentionsByUserId(@Param("opponentId") String opponentId);
//@Query("SELECT new com.knucapstone.rudoori.model.dto.UserScore$OpponentMention(m.mentionId, m.userId.userId, u.nickname, m.content) " +
//        "FROM Mention m " +
//        "INNER JOIN UserInfo u ON m.userId = u " +
//        "WHERE m.opponentId = :opponentId")
//List<UserScore.OpponentMention> findOpponentMentionsByUserId(@Param("opponentId") String opponentId);
//
//
//
//}
