package com.molta.domain.centralBoardState.controller;

import com.molta.domain.centralBoardState.model.DTO.CentralBoardStateDTO;
import com.molta.domain.centralBoardState.model.DTO.RoomDTO;
import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import com.molta.domain.centralBoardState.service.GameSetupService;
import com.molta.domain.centralBoardState.service.RoomService;
import com.molta.domain.centralBoardState.model.DTO.CreateRoomDTO;
import com.molta.domain.gameState.model.entity.GameStateEntity;
import com.molta.domain.gameState.service.GameRoomService;
import com.molta.domain.playerInformation.model.dto.PlayerInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/room")
public class CentralBoardStateController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private GameSetupService gameSetupService;
    @Autowired
    private GameRoomService gameRoomService;
    @Autowired
    private CentralBoardStateRepository centralBoardStateRepository;


    // 방 만들기
    @PostMapping("/create")
    public ResponseEntity<CreateRoomDTO> createRoom(@RequestBody CreateRoomDTO createRoomDTO) {
        try {
            CreateRoomDTO createRoom = roomService.createRoom(createRoomDTO);
            return ResponseEntity.ok(createRoom);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinRoom(@RequestParam String centralBoardId, @RequestParam String playerId) {
        try {
            roomService.joinRoom(centralBoardId, playerId);
            return ResponseEntity.ok("Player joined the room successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 플레이어 준비 상태 설정
    @PostMapping("/set-ready")
    public ResponseEntity<String> setPlayerReady(@RequestParam String centralBoardId, @RequestParam String playerId, @RequestParam boolean isReady) {
        try {
            roomService.setPlayerReady(centralBoardId, playerId, isReady);
            return ResponseEntity.ok("Player ready status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 게임 시작
    @PostMapping("/{centralBoardId}/start")
    public ResponseEntity<Map<String, String>> startGame(@PathVariable String centralBoardId) {
        try {
            String gameId = roomService.startGame(centralBoardId);  // 서비스에서 gameId 반환받기
            Map<String, String> response = new HashMap<>();
            response.put("gameId", gameId);  // gameId를 반환 값에 추가
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error: " + e.getMessage()));
        }
    }

    // 방 나가기 엔드포인트
    @PostMapping("/leave")
    public ResponseEntity<String> leaveRoom(@RequestParam String centralBoardId, @RequestParam String playerId) {
        try {
            roomService.leaveRoom(centralBoardId, playerId);
            return ResponseEntity.ok("Player left the room successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 방 목록 가져오기
    @GetMapping("/list")
    public ResponseEntity<List<RoomDTO>> getRoomList() {
        List<RoomDTO> roomList = roomService.getRoomList();
        return ResponseEntity.ok(roomList);
    }
    // 중앙 보드 상태 조회
    @GetMapping("/{gameId}/board-state")
    public ResponseEntity<CentralBoardStateDTO> getBoardState(@PathVariable String gameId) {
        try {
            CentralBoardStateDTO boardState = gameSetupService.getBoardState(gameId);
            return ResponseEntity.ok(boardState);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 방에 있는 플레이어 정보 조회
    @GetMapping("/{centrolBoardId}/players")
    public ResponseEntity<List<String>> getPlayersInRoom(@PathVariable String centrolBoardId) {
        try {
            List<String> players = gameRoomService.getPlayersInRoom(centrolBoardId);
            return ResponseEntity.ok(players);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
