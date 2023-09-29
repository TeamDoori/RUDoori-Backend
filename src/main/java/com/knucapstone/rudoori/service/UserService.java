package com.knucapstone.rudoori.service;

import com.knucapstone.rudoori.model.dto.User;
import com.knucapstone.rudoori.model.dto.*;

import com.knucapstone.rudoori.model.entity.Block;
import com.knucapstone.rudoori.model.dto.UserInfoDto;
import com.knucapstone.rudoori.model.entity.Score;
import com.knucapstone.rudoori.model.entity.UserInfo;
import com.knucapstone.rudoori.repository.BlockRepository;
import com.knucapstone.rudoori.repository.ScoreRepository;
import com.knucapstone.rudoori.repository.UserRepository;
import com.knucapstone.rudoori.model.entity.*;
import com.knucapstone.rudoori.model.dto.UserInfoDto;
import com.knucapstone.rudoori.repository.*;

import com.knucapstone.rudoori.token.Token;
import com.knucapstone.rudoori.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final BlockRepository blockRepository;
    private final ImageRepository imageRepository;

    @Value("${file.serverDir}")
    private String serverFileDir;
    @Value("${file.dbDir}")
    private String dbFileDir;

    @Transactional
    public boolean deleteUser(UserInfoDto loginInfo) {
        String userId = loginInfo.getUserId();
        String pwd = loginInfo.getPassword();
        boolean equalPwd = false;
        UserInfo userInfo = userRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));
        if (userInfo.isEnabled()) {
            String storedPwd = userInfo.getPassword();
            equalPwd = passwordEncoder.matches(pwd, storedPwd);
            if (equalPwd) {
                userRepository.deleteById(userId);
            }
            return equalPwd;
        }
        return equalPwd;

    }

    @Transactional
    public boolean updatePwd(UserInfoDto updatePwdInfo) {
        String userId = updatePwdInfo.getUserId();
        String pwd = updatePwdInfo.getPassword();
        String updatedPwd = updatePwdInfo.getUpdatedPwd();
        boolean equalPwd = false;
        UserInfo userInfo = userRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));
        if (userInfo.isEnabled()) {
            String storedPwd = userInfo.getPassword();
            equalPwd = passwordEncoder.matches(pwd, storedPwd);
            if (equalPwd) {
                userInfo.setPassword(passwordEncoder.encode(updatedPwd));
            }
            return equalPwd;
        }
        return equalPwd;
    }



    @Transactional(readOnly = true)
    public UserInfoScoreDto getUserProfile(String userId) {
        UserInfo userInfo = userRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));
        Image image = imageRepository.findByUserId(userId).orElse(null);
        String imagePath = null;
        if(image != null){
            imagePath = image.getPath();
        }
        List<Score> scoreList = scoreRepository.findTop5ByUserIdOrderByCreatedDtDesc(userInfo);
        List<String> opponentIds = scoreList.stream()
                .map(Score::getOpponentId)
                .collect(Collectors.toList());

        List<UserInfo> users = userRepository.findByUserIdIn(opponentIds).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));

        List<UserScore.Mention> mentionList = new ArrayList<>();
        Map<String, String> userIdToNicknameMap = users.stream()
                .collect(Collectors.toMap(UserInfo::getUserId, UserInfo::getNickname));
        for (Score score : scoreList) {
            String opponentId = score.getOpponentId();
            String user = userIdToNicknameMap.get(opponentId);
            if (userInfo != null) {
                mentionList.add(UserScore.Mention.builder()
                        .mentionId(score.getScoreId())
                        .mentionUserId(score.getOpponentId())
                        .mentionUserNickname(user)
                        .roomName(score.getRoomName())
                        .content(score.getMention())
                        .score(score.getGrade())
                        .createdDt(score.getCreatedDt())
                        .build());
            }
        }
        String score;

        double point = Optional.ofNullable(userInfo.getScore()).orElse(0.0);

        if (Double.compare(point, 4.5) >= 0) {
            score = "A+";
        } else if (Double.compare(point, 4.5) < 0 && Double.compare(point, 4.0) >= 0) {
            score = "A";
        } else if (Double.compare(point, 4.0) < 0 && Double.compare(point, 3.5) >= 0) {
            score = "B+";
        } else if (Double.compare(point, 3.5) < 0 && Double.compare(point, 3.0) >= 0) {
            score = "B";
        } else if (Double.compare(point, 3.0) < 0 && Double.compare(point, 2.5) >= 0) {
            score = "C+";
        } else if (Double.compare(point, 2.5) < 0 && Double.compare(point, 2.0) >= 0) {
            score = "C";
        } else if (Double.compare(point, 2.0) < 0 && Double.compare(point, 1.5) >= 0) {
            score = "D+";
        } else if (Double.compare(point, 1.5) < 0 && Double.compare(point, 1.0) >= 0) {
            score = "D";
        } else {
            score = "F";
        }

        if (userInfo.isEnabled()) {
            return UserInfoScoreDto.builder()
                    .major(userInfo.getMajor())
                    .nickname(userInfo.getNickname())
                    .score(userInfo.getScore())
                    .image(imagePath)
                    .point(point)
                    .mentions(mentionList)
                    .build();
        }
        return null;
    }


    public User.UserInfoResponse getInfo(String userId) {
        UserInfo user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
        return User.UserInfoResponse
                .builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .nickName(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .major(user.getMajor())
                .build();
    }

    // 회원정보 수정 : 이름,생일,성별,전공,이메일,전화번호,닉네임만 수정가능
    @Transactional
    public boolean updateUserInfo(String userId, User.UpdateInfoRequest updateRequest) {
        UserInfo findInfo = userRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));

//        System.out.println(findInfo);
        if (findInfo.isEnabled()) {
            findInfo.setName(updateRequest.getUserName());
            findInfo.setBirthday(updateRequest.getBirthday());
            findInfo.setGender(updateRequest.getGender());
            findInfo.setMajor(updateRequest.getMajor());
            findInfo.setEmail(updateRequest.getEmail());
            findInfo.setPhoneNumber(updateRequest.getPhoneNumber());
            findInfo.setNickname(updateRequest.getNickname());
//            userRepository.save(findInfo);// 기존 데이터를 수정하는 것으로 이미 영속화 되어있어 따로 save할 필요없다.
            return true;
        } else {
            throw new RuntimeException("자신의 게시글만 삭제할 수 있습니다");
        }


    }

    @Transactional
    public boolean logoutUser(String userId, UserInfo userInfo) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));

        // authorization으로 받은 user와 Param으로 받은 user가 동일한 경우 로그아웃
        if (user.getUserId().equals(userInfo.getUserId())) {
            revokeAllUserTokens(user);
            return true;
        } else {
            throw new RuntimeException("자신만 로그아웃 할 수 있습니다");
        }

    }


    @Transactional
    public String updateProfileImage(String userId, MultipartFile multipartFile) throws IOException {
        Image image = imageRepository.findByUserId(userId).get();
        String imageName = image.getStoreFileName();
        String savedDbPath = null;
        //이미지 업로드 코드
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String filePath = serverFileDir + "profile/" + userId + "/" + imageName;
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("파일이 성공적으로 삭제되었습니다.");
                } else {
                    System.out.println("파일 삭제 실패");
                }
            } else {
                System.out.println("파일이 존재하지 않습니다.");
            }

            String origName = multipartFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String extension = origName.substring(origName.lastIndexOf("."));
            String savedName = uuid + extension;
            String savedServerPath = serverFileDir + "profile/" + userId + "/" + savedName;
             savedDbPath = dbFileDir + "profile/" + userId + "/" + savedName;
//            String savedServerPath = serverFileDir + savedName;
//            String savedDbPath = dbFileDir + savedName;

            image.setPath(savedDbPath);
            image.setStoreFileName(savedName);

            multipartFile.transferTo(new File(savedServerPath));

        }
    return savedDbPath;
    }

    private void revokeAllUserTokens(UserInfo user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Transactional
    public User.BlockResponse blockUserId(User.BlockRequest blockRequest) {
        UserInfo userInfo = userRepository.findByUserId(blockRequest.getUserId()).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));
        Optional<UserInfo> blockedUser = userRepository.findByUserId(blockRequest.getBlockedId());
        if (blockedUser.isPresent()) {
            Block block = Block
                    .builder()
                    .blockedUser(blockRequest.getBlockedId())
                    .userId(userInfo)
                    .build();
            blockRepository.save(block);

            return User.BlockResponse.builder()
                    .blockedId(blockRequest.getBlockedId())
                    .build();
        } else throw new NullPointerException("존재하지 않는 아이디입니다.");
    }


    @Transactional
    public ScoreResponse giveScore(String opponentId, ScoreRequest scoreRequest, UserInfo userinfo) {
        UserInfo opponent = userRepository.findByUserId(opponentId).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));
        UserInfo user = userRepository.findByUserId(userinfo.getUserId()).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));

        if (!opponentId.equals(user.getUserId())) {

            // opponentIdCount: score db에서 찾은 현재까지의 opponentId의 개수
            long opponentIdCount;

            if (!scoreRepository.findByOpponentId(opponentId).isEmpty()) {
                opponentIdCount = scoreRepository.countByOpponentId(opponentId);
            } else {
                opponentIdCount = 0L;
            }

            // avgGrade(평균값): ((opponentIdCount * 현재까지 opponent의 점수) + 더할 점수) / opponentIdCount + 방금 추가한 개수;
            double avgGrade;

//            if (opponent.getScore() != null) {
//                avgGrade = ((opponentIdCount * opponent.getScore()) + scoreRequest.getGrade()) / (opponentIdCount + 1);
//            } else {
//                avgGrade = scoreRequest.getGrade();
//            }

//            Score saveScore = Score.builder()
//                    .userId(user)
//                    .opponentId(opponent.getUserId())
//                    .grade(scoreRequest.getGrade())
//                    .build();
//
//            opponent.setScore(avgGrade);

//            scoreRepository.save(saveScore);

            return ScoreResponse.builder()
                    .opponentNickName(opponent.getNickname())
                    .opponentGrade(opponent.getScore())
                    .build();
        } else {
            throw new RuntimeException("자신은 평가할 수 없습니다!");
        }
    }


    @Transactional
    public UserScore.UserScoreResponse getUserMannerScore(String userId) {
        UserInfo userInfo = userRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));
        List<Score> scoreList = scoreRepository.findTop5ByUserIdOrderByCreatedDtDesc(userInfo);
        List<String> opponentIds = scoreList.stream()
                .map(Score::getOpponentId)
                .collect(Collectors.toList());

        List<UserInfo> users = userRepository.findByUserIdIn(opponentIds).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));

        List<UserScore.Mention> mentionList = new ArrayList<>();
        Map<String, String> userIdToNicknameMap = users.stream()
                .collect(Collectors.toMap(UserInfo::getUserId, UserInfo::getNickname));
        for (Score score : scoreList) {
            String opponentId = score.getOpponentId();
            String user = userIdToNicknameMap.get(opponentId);
            if (userInfo != null) {
                mentionList.add(UserScore.Mention.builder()
                        .mentionId(score.getScoreId())
                        .mentionUserId(score.getOpponentId())
                        .mentionUserNickname(user)
                        .roomName(score.getRoomName())
                        .content(score.getMention())
                        .score(score.getGrade())
                        .createdDt(score.getCreatedDt())
                        .build());
            }
        }
        String score;

        double point = Optional.ofNullable(userInfo.getScore()).orElse(0.0);

        if (Double.compare(point, 4.5) >= 0) {
            score = "A+";
        } else if (Double.compare(point, 4.5) < 0 && Double.compare(point, 4.0) >= 0) {
            score = "A";
        } else if (Double.compare(point, 4.0) < 0 && Double.compare(point, 3.5) >= 0) {
            score = "B+";
        } else if (Double.compare(point, 3.5) < 0 && Double.compare(point, 3.0) >= 0) {
            score = "B";
        } else if (Double.compare(point, 3.0) < 0 && Double.compare(point, 2.5) >= 0) {
            score = "C+";
        } else if (Double.compare(point, 2.5) < 0 && Double.compare(point, 2.0) >= 0) {
            score = "C";
        } else if (Double.compare(point, 2.0) < 0 && Double.compare(point, 1.5) >= 0) {
            score = "D+";
        } else if (Double.compare(point, 1.5) < 0 && Double.compare(point, 1.0) >= 0) {
            score = "D";
        } else {
            score = "F";
        }

        return UserScore.UserScoreResponse.builder()
                .score(score)
                .point(point)
                .mentions(mentionList)
                .build();
    }
}
