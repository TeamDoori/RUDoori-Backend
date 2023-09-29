package com.knucapstone.rudoori.controller;

import com.knucapstone.rudoori.common.ApiResponse;
import com.knucapstone.rudoori.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/email")
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // 임시 비밀번호 발급
    @PostMapping("/password")
    public ApiResponse<String> sendPasswordMail(@RequestParam String email) throws MessagingException {

        return ApiResponse.createSuccess(emailService.sendPw(email));
    }
    //이메일 인증을 위한 인증번호 발급
    @PostMapping("/confirm")
    public ApiResponse<String> confirmEmail(@RequestParam String email) throws MessagingException {
        return ApiResponse.createSuccess(emailService.confirmEmail(email));
    }

}