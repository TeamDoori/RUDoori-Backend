package com.knucapstone.rudoori.service;

import com.knucapstone.rudoori.model.dto.UserInfoDto;
import com.knucapstone.rudoori.model.entity.UserInfo;
import com.knucapstone.rudoori.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String sendPw(String email) throws MessagingException {
        String authNum = createCode();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        UserInfo userInfo = userRepository.findByEmail(email).get();
        userInfo.setPassword(passwordEncoder.encode(authNum));

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(email); // 메일 수신자
        mimeMessageHelper.setSubject("임시 비밀번호 발급"); // 메일 제목
        mimeMessageHelper.setText("임시 비밀번호: " + authNum, false); // 메일 본문 내용, HTML 여부
        javaMailSender.send(mimeMessage);

        log.info("Success");
        return authNum;
    }

    @Transactional
    public String confirmEmail(String email) throws MessagingException {
        String authNum = createCode();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(email); // 메일 수신자
        mimeMessageHelper.setSubject("아에일 인증"); // 메일 제목
        mimeMessageHelper.setText("이메일 인증 번호: " + authNum, false); // 메일 본문 내용, HTML 여부
        javaMailSender.send(mimeMessage);

        return authNum;

    }

    // 인증번호 및 임시 비밀번호 생성 메서드
    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0:
                    key.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int) random.nextInt(26) + 65));
                    break;
                default:
                    key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

}