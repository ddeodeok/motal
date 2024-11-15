package com.molta.config;

import com.molta.domain.centralBoardState.model.DTO.RoomDTO;
import com.molta.domain.centralBoardState.service.RoomService;
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
public class WebSocketeController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private RoomService roomService;

    // 클라이언트에서 메시지를 받으면, 이를 처리하고 "/topic/chat"으로 브로드캐스트
    @MessageMapping("/chat")  // 클라이언트에서 "/app/chat" 경로로 메시지가 오면 이 메서드가 호출됨
    @SendTo("/topic/chat")  // 처리된 메시지를 "/topic/chat" 경로에 구독 중인 클라이언트로 전송
    public String sendMessage(String message) {
        System.out.println("Received message: " + message);
        return message;  // 받은 메시지를 그대로 브로드캐스트
    }
    // 서버에서 플레이어가 방에 들어올 때
    @MessageMapping("/game/{gameId}/join")
    public void playerJoined(String playerId, @DestinationVariable String gameId) {
//        String message = "Player " + playerId + " has joined the game.";
        PlayerJoinedMessage playerJoinedMessage = new PlayerJoinedMessage(playerId, "Player Name");
        messagingTemplate.convertAndSend("/topic/room/" + gameId + "/player-joined", playerJoinedMessage);
    }

    @MessageMapping("/game/{gameId}/leave")
    public void playerLeft(String playerId, @DestinationVariable String gameId) {
//        System.out.println("Player " + playerId + " left the game: " + gameId);
        PlayerLeftMessage playerLeftMessage = new PlayerLeftMessage(playerId, "Player Name");
        messagingTemplate.convertAndSend("/topic/room/" + gameId + "/player-left", playerLeftMessage);
    }

    @MessageMapping("/room/updates")
    public void sendRoomUpdates() {
        List<RoomDTO> updatedRooms = roomService.getRoomList();  // 방 목록을 가져옴
        messagingTemplate.convertAndSend("/topic/room/updates", updatedRooms);
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
