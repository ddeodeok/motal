package com.molta.domain.playerInformation.service;


import com.molta.domain.playerInformation.model.entity.PlayerInformation;
import com.molta.domain.playerInformation.repositiory.PlayerInformationRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class PlayerInformationService {
    @Autowired
    private PlayerInformationRepository playerInformationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //회원 가입
    public String registerUser(String userId, String name ,String nickname  ,String rawPassword) {
        // 중복 검사
        if (playerInformationRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 닉네임입니다.");
        }
        if (playerInformationRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 사용자 엔티티 생성 및 저장
        PlayerInformation newUser = PlayerInformation.builder()
                .userId(userId)
                .name(name)
                .nickname(nickname)
                .password(encodedPassword)
                .createdAt(LocalDateTime.now())
                .build();

        playerInformationRepository.save(newUser);
        return newUser.getUserId();
    }

    // 로그인
    public boolean authenticateUser(String userId, String rawPassword) {
        PlayerInformation user = playerInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return true;
    }

    private static final String SECRET_KEY = "your_secret_key"; // 실제로는 더 안전한 키로 설정

    // 기존 코드와 동일...

    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일 유효기간
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
}
