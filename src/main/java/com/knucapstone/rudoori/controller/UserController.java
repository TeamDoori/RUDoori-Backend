package com.knucapstone.rudoori.controller;

import com.knucapstone.rudoori.common.ApiResponse;
import com.knucapstone.rudoori.model.dto.User;
import com.knucapstone.rudoori.model.entity.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.knucapstone.rudoori.model.dto.*;
import com.knucapstone.rudoori.model.dto.UserInfoDto;
import com.knucapstone.rudoori.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/quit")
    public boolean deleteUser(@RequestBody UserInfoDto loginInfo) {
        return userService.deleteUser(loginInfo);
    }

    @PatchMapping("/pwd")
    public boolean updatePwd(@RequestBody UserInfoDto updatePwdInfo) {
        return userService.updatePwd(updatePwdInfo);
    }

    @GetMapping("/profile/{userId}")
    public UserInfoScoreDto getUserProfile(@PathVariable("userId") String userId) {
        return userService.getUserProfile(userId);
    }

    @GetMapping("/info")
    public ApiResponse<User.UserInfoResponse> getUserInfo(@RequestParam String userId) {
        return ApiResponse.createSuccess(userService.getInfo(userId));
    }


    @PatchMapping("/info/update/{userId}")
    public boolean updateUserInfo
            (@PathVariable("userId")String userId, @RequestBody User.UpdateInfoRequest updateRequest) {
        return userService.updateUserInfo(userId, updateRequest);
    }

    @PatchMapping("/profileImage/{userId}")
    public ResponseEntity<String> updateProfileImage
            (@PathVariable("userId")String userId,
             @RequestPart MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok( userService.updateProfileImage(userId, multipartFile));
    }

    @PostMapping("/logout/{userId}")
    public ApiResponse<Boolean> logoutUser(@PathVariable("userId")String userId, @AuthenticationPrincipal UserInfo userInfo){
        return ApiResponse.createSuccess(userService.logoutUser(userId, userInfo));
    }
//    @PostMapping("/write/mention/{opponentId}")
//    public ApiResponse<MentionDto.MentionResponse> mentionForMan(@PathVariable("opponentId") String opponentId, @RequestBody MentionDto.MentionRequest mentionRequest,  @AuthenticationPrincipal UserInfo userInfo) {
//        return ApiResponse.createSuccess(userService.mentionForMan(opponentId, mentionRequest, userInfo));
//    }

    @PostMapping("/block/person")
    public ApiResponse<User.BlockResponse> blockUserId(@RequestBody @Valid User.BlockRequest blockRequest) {
        return ApiResponse.createSuccess(userService.blockUserId(blockRequest));
    }

    // 다른사람 점수주기
    @PostMapping("/score/{opponentId}")
    public ApiResponse<ScoreResponse> giveScore(@PathVariable("opponentId")String opponentId, @RequestBody ScoreRequest scoreRequest,  @AuthenticationPrincipal UserInfo userInfo){
        return ApiResponse.createSuccess(userService.giveScore(opponentId, scoreRequest, userInfo));
    }

    // 내정보 점수, 평가내용 보기
    @GetMapping("/info/score")
    public ApiResponse<UserScore.UserScoreResponse> getUserMannerScore(@RequestParam String userId) {
        return ApiResponse.createSuccess(userService.getUserMannerScore(userId));
    }

}

