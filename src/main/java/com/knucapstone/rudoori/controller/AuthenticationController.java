package com.knucapstone.rudoori.controller;

import com.knucapstone.rudoori.common.ApiResponse;
import com.knucapstone.rudoori.model.dto.Authentication;
import com.knucapstone.rudoori.model.entity.UserInfo;
import com.knucapstone.rudoori.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

//    @PostMapping("/register")
//    public ResponseEntity<Authentication.RegisterResponse> register(
//            @Valid @RequestPart(required = false) Authentication.RegisterRequest request,
//            @RequestPart(required = false) MultipartFile multipartFile
//    ) throws IOException {
//        System.out.println("==============");
//        System.out.println("request: "+request);
//        System.out.println("multipartFile: "+multipartFile);
//        System.out.println("=============");
//        return ResponseEntity.ok(service.register(multipartFile, request));
//    }

    @PostMapping("/register")
    public ResponseEntity<Authentication.RegisterResponse> register(
            @Valid @RequestBody Authentication.RegisterRequest request
    ) throws IOException {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/register/profile")
    public ResponseEntity<String> registerImage(
            @AuthenticationPrincipal UserInfo userinfo,
            @RequestPart(required = false) MultipartFile multipartFile
    ) throws IOException{
        System.out.println("userinfo1: "+userinfo);
        System.out.println("multipartFile1: "+multipartFile);
        return ResponseEntity.ok(service.registerImage(userinfo, multipartFile));
    }

    @PostMapping("/login")
    public ApiResponse<Authentication.AuthenticationResponse> login(
            @RequestBody Authentication.AuthenticationRequest request
    ){
        return ApiResponse.createSuccess(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
        service.refreshToken(request, response);
    }
}
