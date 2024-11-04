package com.molta.domain.playerInformation.controller;


import com.molta.common.JwtTokenProvider;
import com.molta.domain.playerInformation.model.dto.LoginRequest;
import com.molta.domain.playerInformation.model.dto.RegisterRequestDto;
import com.molta.domain.playerInformation.model.entity.PlayerInformation;
import com.molta.domain.playerInformation.repositiory.PlayerInformationRepository;
import com.molta.domain.playerInformation.service.PlayerInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class PlayerInformationController {

    @Autowired
    private PlayerInformationService playerInformationService;
    @Autowired
    private PlayerInformationRepository playerInformationRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDto requestDto) {
        try {
            playerInformationService.registerUser(requestDto.getUserId(), requestDto.getName(), requestDto.getNickname(), requestDto.getPassword());
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원가입 오류: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String password = loginRequest.getPassword();
        // 인증 및 토큰 생성 로직
        String token = jwtTokenProvider.generateToken(userId);
        // 사용자 정보에서 닉네임 가져오기 (PlayerInformationRepository를 통해 조회)
        PlayerInformation playerInfo = playerInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 토큰이 생성되었다면 JSON 형식으로 응답 반환
        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("nickname", playerInfo.getNickname());
            response.put("playerId", playerInfo.getUserId());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }
}
