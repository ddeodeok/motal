package com.molta.config;

import com.molta.domain.centralBoardState.model.DTO.RoomDTO;
import com.molta.domain.centralBoardState.service.RoomService;
import com.molta.domain.gameState.model.entity.GameStateEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 클라이언트에서 메시지를 받으면, 이를 처리하고 "/topic/chat"으로 브로드캐스트
    @MessageMapping("/chat")  // 클라이언트에서 "/app/chat" 경로로 메시지가 오면 이 메서드가 호출됨
    @SendTo("/topic/chat")  // 처리된 메시지를 "/topic/chat" 경로에 구독 중인 클라이언트로 전송
    public String sendMessage(String message) {
        System.out.println("Received message: " + message);
        return message;  // 받은 메시지를 그대로 브로드캐스트
    }
    // 서버에서 플레이어가 방에 들어올 때
    @MessageMapping("/game/{centralBoardId}/join")
    public void playerJoined(String playerId, @DestinationVariable String centralBoardId) {
//        String message = "Player " + playerId + " has joined the game.";
        PlayerJoinedMessage playerJoinedMessage = new PlayerJoinedMessage(playerId, "Player Name");
        messagingTemplate.convertAndSend("/topic/room/" + centralBoardId + "/player-joined", playerJoinedMessage);
    }

    @MessageMapping("/game/{centralBoardId}/leave")
    public void playerLeft(String playerId, @DestinationVariable String centralBoardId) {
//        System.out.println("Player " + playerId + " left the game: " + gameId);
        PlayerLeftMessage playerLeftMessage = new PlayerLeftMessage(playerId, "Player Name");
        messagingTemplate.convertAndSend("/topic/room/" + centralBoardId + "/player-left", playerLeftMessage);
    }

    @MessageMapping("/room/updates")
    public void sendRoomUpdates() {
        // 방 목록 업데이트를 위한 메시지 전송
        messagingTemplate.convertAndSend("/topic/room/updates", "Room List Updated");
    }

    // 현재 플레이어를 클라이언트에 전송하는 메서드
    public void sendCurrentPlayerUpdate(String gameId, String playerId) {
        // 메시지를 보내고자 하는 목적지 (예: "/topic/game/{gameId}/current-player")
        String destination = "/topic/game/" + gameId + "/current-player";

        // 클라이언트로 보낼 메시지 (게임 ID와 플레이어 ID를 전송)
        CurrentPlayerUpdateMessage message = new CurrentPlayerUpdateMessage(gameId, playerId);

        // 메시지를 구독 중인 클라이언트에게 전송
        messagingTemplate.convertAndSend(destination, message);
    }

    // 게임 상태 업데이트 전송
    public void sendGameStateUpdate(String centralBoardId, GameStateEntity gameState) {
        // 게임 상태를 업데이트하려는 클라이언트들의 대상 (게임 ID와 관련된 경로로 메시지 전송)
        String destination = "/topic/room/" + centralBoardId + "/state";
        System.out.println("Sending message to: " + destination);
        messagingTemplate.convertAndSend(destination, gameState);
    }

    public void sendGameIdToPlayers(String centralBoardId, String gameId) {
        messagingTemplate.convertAndSend("/topic/room/" + centralBoardId + "/game-start", gameId);
        System.out.println("Received message: " + gameId);
    }


    @Data
    @NoArgsConstructor
    // 메시지 객체 (플레이어가 방에 들어왔을 때)
    public static class PlayerJoinedMessage {
        private String type = "PLAYER_JOINED";
        private String playerId;
        private String playerName;

        // 생성자
        public PlayerJoinedMessage(String playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
        }
    }

    @Data
    @NoArgsConstructor
    // 메시지 객체 (플레이어가 방에 들어왔을 때)
    public static class PlayerLeftMessage  {
        private String type = "PLAYER_LEFT";
        private String playerId;
        private String playerName;

        // 생성자
        public PlayerLeftMessage (String playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
        }
    }
}
