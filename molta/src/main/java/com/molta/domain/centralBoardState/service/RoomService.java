package com.molta.domain.centralBoardState.service;


//import com.molta.config.GameRoomWebSocketHandler;
import com.molta.domain.cardDefinition.model.entity.CardDefinitionEntity;
import com.molta.domain.cardDefinition.repository.CardDefinitionRepository;
import com.molta.config.WebSocketController;
import com.molta.domain.centralBoardState.model.DTO.CreateRoomDTO;
import com.molta.domain.centralBoardState.model.DTO.RoomDTO;
import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private CentralBoardStateRepository centralBoardStateRepository;
    @Autowired
    private CardDefinitionRepository cardDefinitionRepository;
    @Autowired
    private GameSetupService gameSetupService;
    @Autowired
    private WebSocketController webSocketController;




    // 방 만들기 로직
    public CreateRoomDTO createRoom(CreateRoomDTO createRoomDTO) {
        String playerId = createRoomDTO.getPlayerId();
        String roomName = createRoomDTO.getGameId();
        String nickname = createRoomDTO.getNickname();
        CentralBoardStateEntity centralBoard = new CentralBoardStateEntity();
        // 방장 준비 상태를 "준비 완료"로 설정
        boolean isReady = true;

        // 기능 카드와 자원 카드 초기화
        List<CardDefinitionEntity> allFunctionCards  = cardDefinitionRepository.findAllByCardType("FUNCTION");
        // `numberOfCards`를 고려하여 전체 기능 카드 목록을 준비합니다.
        List<CardDefinitionEntity> functionDeck = new ArrayList<>();
        for (CardDefinitionEntity card : allFunctionCards) {
            for (int i = 0; i < card.getNumberOfCards(); i++) {
                functionDeck.add(card);
            }
        }
        Collections.shuffle(functionDeck);
        // 오픈된 기능 카드 2장과 남은 기능 카드 덱 설정
        centralBoard.setFunctionDeck(functionDeck.subList(2, functionDeck.size()).stream()
                .map(card -> card.getId().intValue()) // int로 변환
                .collect(Collectors.toList()));
        centralBoard.setFunctionCards(functionDeck.subList(0, 2).stream()
                .map(card -> card.getId().intValue())
                .collect(Collectors.toList()));
        centralBoard.setDiscardedFunctionCards(new ArrayList<>()); // 빈 리스트로 초기화


        List<CardDefinitionEntity> allResourceDeck = cardDefinitionRepository.findAllByCardType("RESOURCE");
        List<CardDefinitionEntity> resourceDeck = new ArrayList<>();
        for (CardDefinitionEntity card : allResourceDeck) {
            for (int i = 0; i < card.getNumberOfCards(); i++) {
                resourceDeck.add(card);
            }
        }
        Collections.shuffle(resourceDeck);
        // 오픈된 자원 카드 4장과 남은 자원 카드 덱 설정
        centralBoard.setResourceDeck(resourceDeck.subList(4, resourceDeck.size()).stream()
                .map(card -> card.getId().intValue())
                .collect(Collectors.toList()));
        centralBoard.setResourceCards(resourceDeck.subList(0, 4).stream()
                .map(card -> card.getId().intValue())
                .collect(Collectors.toList()));
        centralBoard.setDiscardedResourceCards(new ArrayList<>());

        // 생성자 플레이어 ID 저장 (게임 생성자)
        centralBoard.setCreatorPlayerId(playerId);
        // 방장이 플레이어 목록에 자동으로 추가되도록 설정
        centralBoard.getPlayers().add(playerId);
        centralBoard.setRoomName(roomName);
        centralBoard.setReady(isReady); // 방장 준비 상태 설정
        centralBoard.updateLastActivity();

        // 방 저장
        centralBoard = centralBoardStateRepository.save(centralBoard);
        // 방 생성 후 방 목록을 실시간으로 업데이트
        webSocketController.sendRoomUpdates();

        // 자원 카드 덱과 오픈된 자원 카드 수를 계산하여 DTO에 담아 반환
        int resourceDeckCount = centralBoard.getResourceDeck().size();
        int functionDeckCount = centralBoard.getFunctionDeck().size();

        CreateRoomDTO responseDto = new CreateRoomDTO();
        responseDto.setResourceCards(centralBoard.getResourceCards());
        responseDto.setRoomName(roomName);
        responseDto.setPlayerId(playerId);
        responseDto.setNickname(nickname);
        responseDto.setResourceDeckCount(resourceDeckCount);
        responseDto.setFunctionDeckCount(functionDeckCount);
        responseDto.setCentralBoardStateId(centralBoard.getId());
        return responseDto;
    }



        // 플레이어 방 참가 로직
    public void joinRoom(String centralBoardId, String playerId) {
        // 게임 ID로 중앙 보드 상태 조회
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findById(centralBoardId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 이미 참가한 플레이어인지 확인
        if (centralBoard.getPlayers().contains(playerId)) {
            throw new IllegalStateException("Player is already in the room");
        }

        // 플레이어 추가
        centralBoard.addPlayer(playerId);
        centralBoard.getPlayers().add(playerId);
        centralBoard.updateLastActivity();
        centralBoardStateRepository.save(centralBoard);
        // 플레이어가 방에 들어오면 웹소켓을 통해 다른 클라이언트에게 알림
        webSocketController.playerJoined(playerId, centralBoardId);
        webSocketController.sendRoomUpdates();
    }

    public String startGame(String centralBoardId) {
        // 게임 ID로 중앙 보드 상태 조회
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findById(centralBoardId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 플레이어 수 검사
        int playerCount = centralBoard.getPlayers().size();
        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalStateException("Game requires 2 to 4 players to start");
        }
        // 모든 플레이어가 준비 완료 상태인지 검사
        if (!centralBoard.getPlayerReadyStatus().values().stream().allMatch(Boolean::booleanValue)) {
            throw new IllegalStateException("All players must be ready to start the game");
        }
        // 새로운 게임 ID 생성
        String gameId = generateNewGameId(centralBoardId); // 고유한 gameId 생성 메서드
        // 게임 초기화 작업 호출
        gameSetupService.initializeGame(gameId, centralBoard.getPlayers(), centralBoardId); // 초기화 메서드 호출하여 중앙 보드와 플레이어 상태 설정
        // 게임 시작 설정
        centralBoard.setGameId(gameId);
        centralBoard.setStarted(true);
        centralBoard.updateLastActivity();
        centralBoardStateRepository.save(centralBoard);

        webSocketController.sendGameIdToPlayers(centralBoardId, gameId);
        webSocketController.sendRoomUpdates();
        return gameId;
    }

    private String generateNewGameId(String playerId) {
        // 현재 시간(밀리초)와 playerId를 조합하여 고유한 gameId 생성
        return System.currentTimeMillis() + playerId;
    }

    // 플레이어 준비 상태 업데이트
    public void setPlayerReady(String centralBoardId, String playerId, boolean isReady) {
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findById(centralBoardId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (!centralBoard.getPlayers().contains(playerId)) {
            throw new IllegalStateException("Player is not in the room");
        }

        // 플레이어의 준비 상태 업데이트
        centralBoard.getPlayerReadyStatus().put(playerId, isReady);
        centralBoard.updateLastActivity();
        centralBoardStateRepository.save(centralBoard);
    }

    // 방 나가기 메서드
    public void leaveRoom(String centralBoardId, String playerId) {
        // 방 나가기 로직 작성
        // 예: 해당 플레이어를 방에서 제거하고 DB를 업데이트
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findById(centralBoardId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        centralBoard.getPlayers().remove(playerId);  // 플레이어 목록에서 해당 플레이어 제거
        if (centralBoard.getPlayers().isEmpty()) {
            // 플레이어가 없으면 방 삭제
            centralBoardStateRepository.delete(centralBoard);
        } else {
            // 변경 사항 저장 (방에 다른 플레이어가 남아있는 경우)
            centralBoard.updateLastActivity();
            centralBoardStateRepository.save(centralBoard);
        }

        webSocketController.playerLeft(playerId, centralBoardId);
        webSocketController.sendRoomUpdates();
    }

    public List<RoomDTO> getRoomList() {
        List<CentralBoardStateEntity> rooms = centralBoardStateRepository.findAll();
        // 방 목록을 RoomDTO 리스트로 변환
        return rooms.stream().map(room ->
                new RoomDTO(
                        room.getId(),
                        room.getGameId(),
                        room.getRoomName(),
                        room.isStarted(), // 게임 시작 여부
                        room.getCreatorPlayerId(), // 방장 ID
                        room.getPlayers().size() // 플레이어 수
                )
        ).collect(Collectors.toList());
    }
}
