package com.molta.domain.gameState.service;


import com.molta.config.WebSocketController;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.gameState.model.entity.GameStateEntity;
import com.molta.domain.gameState.repository.GameStateRepository;
import com.molta.domain.gemCardDefinition.model.entity.GemCardDefinitionEntity;
import com.molta.domain.gemCardDefinition.repository.GemCardDefinitionRepository;
import com.molta.domain.gameState.service.GameTurnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PlayerTakeCardsService {

    @Autowired
    private GameTurnService gameTurnService;
    @Autowired
    private GameStateRepository gameStateRepository;
    @Autowired
    private CentralBoardStateRepository centralBoardStateRepository;
    @Autowired
    private GemCardDefinitionRepository cardDefinitionRepository;
    @Autowired
    private WebSocketController webSocketController;


    // 자원 카드를 가져오는 메서드
    public void takeResourceCard(String centralBoardId, String gameId, String playerId,
                                 int cardId, boolean isFromDeck, int index) {

        GameStateEntity gameState = gameStateRepository.findByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new IllegalArgumentException("Game state not found"));

        // 현재 플레이어인지 확인
        String currentPlayerId = gameState.getCurrentPlayer();
        if(!playerId.equals(currentPlayerId)) {
            throw new IllegalStateException("현재 플레이어가 아닙니다.");
        }

        if (gameState.getAction() <= 0) {
            throw new IllegalStateException("No actions remaining for this turn");
        }
        // 게임 ID를 사용해 중앙 보드 상태를 조회
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalStateException("Central board state not found"));

        Integer selectedCardValue = null;

        // 중앙의 오픈된 자원 카드 중에서 선택
        if (!isFromDeck) {
            // 클릭한 카드의 인덱스를 기반으로 해당 카드만 삭제
            if (centralBoard.getResourceCards().size() > index) {
                Integer cardToRemove = centralBoard.getResourceCards().get(index);

                // 선택된 카드가 있는 경우 삭제
                if (cardToRemove != null && cardToRemove.equals(cardId)) {
                    // 해당 인덱스에서 카드 제거
                    centralBoard.getResourceCards().remove(index); // 인덱스로 정확한 카드 삭제
                } else {
                    throw new IllegalStateException("The specified resource card is not available in the open cards");
                }

                // 선택된 카드 값 저장
                selectedCardValue = cardId;
            } else {
                throw new IllegalArgumentException("Invalid index for resource card");
            }
        } else {
            // 더미에서 자원 카드 무작위로 가져오기
            if (!centralBoard.getResourceDeck().isEmpty()) {
                selectedCardValue = centralBoard.getResourceDeck().remove(0);  // 덱에서 첫 번째 카드 제거
            } else {
                // 덱이 비었으면, 버려진 카드 더미에서 섞어서 덱에 넣음
                if (!centralBoard.getDiscardedResourceCards().isEmpty()) {
                    Collections.shuffle(centralBoard.getDiscardedResourceCards());
                    centralBoard.getResourceDeck().addAll(centralBoard.getDiscardedResourceCards());
                    centralBoard.getDiscardedResourceCards().clear(); // 버려진 카드 목록 초기화
                } else {
                    throw new IllegalStateException("No resource cards available in the deck or discarded cards");
                }
            }
        }

        // 덱에서 새 카드를 가져와서 해당 인덱스에 추가
        if (selectedCardValue != null) {
            if (!centralBoard.getResourceDeck().isEmpty()) {
                Integer newCard = centralBoard.getResourceDeck().remove(0);  // 덱에서 새 카드 가져오기
                // 덱이 비었으면, 버려진 카드 더미에서 섞어서 덱에 넣음
                if (centralBoard.getResourceDeck().isEmpty()) {
                    if (!centralBoard.getDiscardedResourceCards().isEmpty()) {
                        Collections.shuffle(centralBoard.getDiscardedResourceCards());
                        centralBoard.getResourceDeck().addAll(centralBoard.getDiscardedResourceCards());
                        centralBoard.getDiscardedResourceCards().clear(); // 버려진 카드 목록 초기화
                    }
                }
                // 해당 인덱스 위치에 새 카드 삽입
                centralBoard.getResourceCards().add(index, newCard);  // 카드 삽입
            } else {
                throw new IllegalStateException("No resource cards available in the deck to refill");
            }
        }

        // 가져온 자원 카드의 수량을 업데이트
        incrementResourceCardCount(gameState, selectedCardValue);

        // 행동 소모
        gameState.setAction(gameState.getAction() - 1);
        gameStateRepository.save(gameState);
        centralBoard.updateLastActivity();
        centralBoardStateRepository.save(centralBoard);
        // 턴 종료 체크 및 턴 넘기기
        if (gameState.getAction() <= 0) {
            gameTurnService.endTurn(gameId, playerId);
        }
        webSocketController.sendGameStateUpdate(centralBoardId, gameState);

    }

    // 자원 카드 보유 수량을 증가시키는 메서드
    private void incrementResourceCardCount(GameStateEntity gameState, int cardValue) {
        switch (cardValue) {
            case 41 -> gameState.setResourceCard1Count(gameState.getResourceCard1Count() + 1);
            case 42 -> gameState.setResourceCard2Count(gameState.getResourceCard2Count() + 1);
            case 43 -> gameState.setResourceCard3Count(gameState.getResourceCard3Count() + 1);
            case 44 -> gameState.setResourceCard4Count(gameState.getResourceCard4Count() + 1);
            case 45 -> gameState.setResourceCard5Count(gameState.getResourceCard5Count() + 1);
            case 46 -> gameState.setResourceCard6Count(gameState.getResourceCard6Count() + 1);
            case 47 -> gameState.setResourceCard7Count(gameState.getResourceCard7Count() + 1);
            case 48 -> gameState.setResourceCard8Count(gameState.getResourceCard8Count() + 1);
            default -> throw new IllegalArgumentException("Invalid resource card value");
        }
    }


    public void takeFunctionCardToGate(String gameId, String playerId, Long functionCardId, boolean isFromDeck) {
        GameStateEntity gameState = gameStateRepository.findByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new IllegalArgumentException("Game state not found"));

        if (gameState.getAction() <= 0) {
            throw new IllegalStateException("No actions remaining for this turn");
        }

        if (gameState.getReadyRevealCard1() != null && gameState.getReadyRevealCard2() != null) {
            throw new IllegalStateException("Gate is full");
        }

        // 게임 ID를 기준으로 중앙 보드 상태를 조회
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findById(gameId)
                .orElseThrow(() -> new IllegalStateException("Central board state not found"));

        GemCardDefinitionEntity card;
        if (isFromDeck) {
            // 더미에서 기능 카드 가져오기
            if (centralBoard.getFunctionDeck().isEmpty()) {
                // 더미가 비었을 경우, 버려진 카드들을 섞어서 더미에 추가
                if (!centralBoard.getDiscardedFunctionCards().isEmpty()) {
                    Collections.shuffle(centralBoard.getDiscardedFunctionCards());
                    centralBoard.getFunctionDeck().addAll(centralBoard.getDiscardedFunctionCards());
                    centralBoard.getDiscardedFunctionCards().clear();
                } else {
                    throw new IllegalStateException("No function cards available in the deck or discard pile");
                }
            }
            Long cardId = centralBoard.getFunctionDeck().remove(0).longValue();
            card = cardDefinitionRepository.findById(cardId)
                    .orElseThrow(() -> new IllegalStateException("Function card not found"));
        } else {
            // 중앙의 오픈된 기능 카드 중에서 가져오기
            Long cardId = centralBoard.getFunctionCards().stream()
                    .filter(c -> c.equals(functionCardId.intValue()))
                    .findFirst()
                    .map(Integer::longValue)
                    .orElseThrow(() -> new IllegalStateException("Function card not available"));
            centralBoard.getFunctionCards().remove(cardId.intValue());

            card = cardDefinitionRepository.findById(cardId)
                    .orElseThrow(() -> new IllegalStateException("Function card not found"));

            // 오픈된 카드가 비워졌으므로 더미에서 새 카드를 추가
            if (centralBoard.getFunctionDeck().isEmpty() && !centralBoard.getDiscardedFunctionCards().isEmpty()) {
                // 더미가 비어 있으면, 버려진 카드들을 섞어서 더미에 추가
                Collections.shuffle(centralBoard.getDiscardedFunctionCards());
                centralBoard.getFunctionDeck().addAll(centralBoard.getDiscardedFunctionCards());
                centralBoard.getDiscardedFunctionCards().clear();
            }
            if (!centralBoard.getFunctionDeck().isEmpty()) {
                Integer newCardId = centralBoard.getFunctionDeck().remove(0);
                centralBoard.getFunctionCards().add(newCardId);
            }
        }

        // 관문에 카드 추가
        if (gameState.getReadyRevealCard1() == null) {
            gameState.setReadyRevealCard1(Integer.parseInt(card.getId())); // String을 int로 변환
        } else {
            gameState.setReadyRevealCard2(Integer.parseInt(card.getId()));
        }

        // 행동 소모
        gameState.setAction(gameState.getAction() - 1);
        gameStateRepository.save(gameState);
        centralBoard.updateLastActivity();
        centralBoardStateRepository.save(centralBoard);
        // 턴 종료 체크 및 턴 넘기기
        if (gameState.getAction() <= 0) {
            gameTurnService.endTurn(gameId, playerId);
        }
    }


}
