package com.molta.domain.centralBoardState.controller;

import com.molta.domain.centralBoardState.service.RoomService;
import com.molta.domain.centralBoardState.model.DTO.CreateRoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
public class CentralBoardStateController {
    @Autowired
    private RoomService roomService;


    // 방 만들기
    @PostMapping("/create")
    public ResponseEntity<String> createRoom(@RequestBody CreateRoomDTO createRoomDTO) {
        try {
            String centralBoardId = roomService.createRoom(createRoomDTO);
            return ResponseEntity.ok(centralBoardId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinRoom(@RequestParam String gameId, @RequestParam String playerId) {
        try {
            roomService.joinRoom(gameId, playerId);
            return ResponseEntity.ok("Player joined the room successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 플레이어 준비 상태 설정
    @PostMapping("/set-ready")
    public ResponseEntity<String> setPlayerReady(@RequestParam String gameId, @RequestParam String playerId, @RequestParam boolean isReady) {
        try {
            roomService.setPlayerReady(gameId, playerId, isReady);
            return ResponseEntity.ok("Player ready status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 게임 시작
    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestParam String gameId) {
        try {
            roomService.startGame(gameId);
            return ResponseEntity.ok("Game started successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 방 나가기 엔드포인트
    @PostMapping("/leave")
    public ResponseEntity<String> leaveRoom(@RequestParam String gameId, @RequestParam String playerId) {
        try {
            roomService.leaveRoom(gameId, playerId);
            return ResponseEntity.ok("Player left the room successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}
