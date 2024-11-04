package com.molta.domain.centralBoardState.service;


import com.molta.domain.cardDefinition.model.entity.CardDefinitionEntity;
import com.molta.domain.cardDefinition.repository.CardDefinitionRepository;
import com.molta.domain.centralBoardState.model.DTO.CreateRoomDTO;
import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private CentralBoardStateRepository centralBoardStateRepository;

    @Autowired
    private CardDefinitionRepository cardDefinitionRepository;

    // 방 만들기 로직
    public String createRoom(CreateRoomDTO createRoomDTO) {
        String playerId = createRoomDTO.getPlayerId();
//        String gameId = createRoomDTO.getGameId();
        String nickname = createRoomDTO.getNickname();
        CentralBoardStateEntity centralBoard = new CentralBoardStateEntity();

        // 새로운 게임 ID 생성
        String gameId = generateNewGameId(playerId); // 고유한 gameId 생성 메서드

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
        centralBoard.setResourceDeck(resourceDeck.subList(4, resourceDeck.size()).stream()
                .map(card -> card.getId().intValue())
                .collect(Collectors.toList()));
        centralBoard.setResourceCards(resourceDeck.subList(0, 4).stream()
                .map(card -> card.getId().intValue())
                .collect(Collectors.toList()));
        centralBoard.setDiscardedResourceCards(new ArrayList<>());

        // 생성된 gameId 설정
        centralBoard.setGameId(gameId);

        // 생성자 플레이어 ID 저장 (게임 생성자)
        centralBoard.setCreatorPlayerId(playerId);

        // 방장이 플레이어 목록에 자동으로 추가되도록 설정
        centralBoard.getPlayers().add(playerId);

        // 방 저장
        centralBoard = centralBoardStateRepository.save(centralBoard);
        return centralBoard.getId(); // 생성된 중앙 보드 ID 반환
    }

    private String generateNewGameId(String playerId) {
        // 현재 시간(밀리초)와 playerId를 조합하여 고유한 gameId 생성
        return System.currentTimeMillis() + playerId;
    }

        // 플레이어 방 참가 로직
    public void joinRoom(String gameId, String playerId) {
        // 게임 ID로 중앙 보드 상태 조회
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 이미 참가한 플레이어인지 확인
        if (centralBoard.getPlayers().contains(playerId)) {
            throw new IllegalStateException("Player is already in the room");
        }

        // 플레이어 추가
        centralBoard.getPlayers().add(playerId);
        centralBoardStateRepository.save(centralBoard);
    }
    public void startGame(String gameId) {
        // 게임 ID로 중앙 보드 상태 조회
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findByGameId(gameId)
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

        // 게임 시작 설정
        centralBoard.setStarted(true);
        centralBoardStateRepository.save(centralBoard);
    }

    // 플레이어 준비 상태 업데이트
    public void setPlayerReady(String gameId, String playerId, boolean isReady) {
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (!centralBoard.getPlayers().contains(playerId)) {
            throw new IllegalStateException("Player is not in the room");
        }

        // 플레이어의 준비 상태 업데이트
        centralBoard.getPlayerReadyStatus().put(playerId, isReady);
        centralBoardStateRepository.save(centralBoard);
    }

    // 방 나가기 메서드
    public void leaveRoom(String roomId, String playerId) {
        // 방 나가기 로직 작성
        // 예: 해당 플레이어를 방에서 제거하고 DB를 업데이트
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        centralBoard.getPlayers().remove(playerId);  // 플레이어 목록에서 해당 플레이어 제거
        centralBoardStateRepository.save(centralBoard); // 변경 사항 저장
    }
}
