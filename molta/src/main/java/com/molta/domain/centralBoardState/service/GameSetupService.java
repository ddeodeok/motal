package com.molta.domain.centralBoardState.service;

import com.molta.domain.cardDefinition.model.entity.CardDefinitionEntity;
import com.molta.domain.cardDefinition.repository.CardDefinitionRepository;
import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import com.molta.domain.gameState.model.entity.GameStateEntity;
import com.molta.domain.gameState.repository.GameStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class GameSetupService {
    @Autowired
    private CentralBoardStateRepository centralBoardStateRepository;
    @Autowired
    private GameStateRepository gameStateRepository;
    @Autowired
    private CardDefinitionRepository cardDefinitionRepository;

    // 게임 초기화 및 플레이어 준비 메서드
    public void initializeGame(String gameId, List<String> playerIds) {
        // 중앙 보드 초기화
        CentralBoardStateEntity centralBoard = setupCentralBoard();
        centralBoard.setGameId(gameId);
        centralBoardStateRepository.save(centralBoard);

        // 플레이어 초기화
        initializePlayers(gameId, playerIds);

        // 선 플레이어 결정
        String firstPlayerId = determineFirstPlayer(playerIds);
        System.out.println("First player ID: " + firstPlayerId);
    }

    // 중앙 보드 셋업
    private CentralBoardStateEntity setupCentralBoard() {
        CentralBoardStateEntity centralBoard = new CentralBoardStateEntity();

        // 기능 카드 셋업
        List<CardDefinitionEntity> functionDeck = cardDefinitionRepository.findAllByCardType("FUNCTION");
        Collections.shuffle(functionDeck);
        // 기능 카드 더미와 오픈된 기능 카드 설정
        centralBoard.getFunctionDeck().addAll(
                functionDeck.subList(2, functionDeck.size()).stream()
                        .map(CardDefinitionEntity::getId) // 카드 엔티티에서 ID 값 추출
                        .map(Long::intValue) // Long 타입을 Integer로 변환
                        .toList()
        );
        centralBoard.getFunctionCards().addAll(
                functionDeck.subList(0, 2).stream()
                        .map(CardDefinitionEntity::getId)
                        .map(Long::intValue)
                        .toList()
        );

        // 자원 카드 셋업
        List<CardDefinitionEntity> resourceDeck = cardDefinitionRepository.findAllByCardType("RESOURCE");
        Collections.shuffle(resourceDeck);
        // 자원 카드 더미와 오픈된 자원 카드 설정
        centralBoard.getResourceDeck().addAll(
                resourceDeck.subList(4, resourceDeck.size()).stream()
                        .map(CardDefinitionEntity::getId)
                        .map(Long::intValue)
                        .toList()
        );
        centralBoard.getResourceCards().addAll(
                resourceDeck.subList(0, 4).stream()
                        .map(CardDefinitionEntity::getId)
                        .map(Long::intValue)
                        .toList()
        );

        // 버려진 카드 초기화
        centralBoard.getDiscardedFunctionCards().clear();
        centralBoard.getDiscardedResourceCards().clear();

        return centralBoard;
    }

    // 플레이어 초기화
    private void initializePlayers(String gameId, List<String> playerIds) {
        for (String playerId : playerIds) {
            GameStateEntity gameState = new GameStateEntity();
            gameState.setGameId(gameId);
            gameState.setPlayerId(playerId);
            gameState.setAction(3); // 초기 행동 수 설정
            gameStateRepository.save(gameState);
        }
    }

    // 선 플레이어 결정
    private String determineFirstPlayer(List<String> playerIds) {
        Random random = new Random();
        int index = random.nextInt(playerIds.size());
        return playerIds.get(index);
    }
}
