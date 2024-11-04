package com.molta.domain.gameState.service;


import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.gameState.model.entity.GameStateEntity;
import com.molta.domain.gameState.repository.GameStateRepository;
import com.molta.domain.gemCardDefinition.model.entity.GemCardDefinitionEntity;
import com.molta.domain.gemCardDefinition.repository.GemCardDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PlayerActionService {

    @Autowired
    private GameStateRepository gameStateRepository;
    @Autowired
    private CentralBoardStateRepository centralBoardStateRepository;
    @Autowired
    private GemCardDefinitionRepository cardDefinitionRepository;


    // 자원 카드를 가져오는 메서드
    public void takeResourceCard(Long id, int cardValue, boolean isFromDeck) {
        GameStateEntity gameState = gameStateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game state not found"));

        if (gameState.getAction() <= 0) {
            throw new IllegalStateException("No actions remaining for this turn");
        }
        // 게임 ID를 사용해 중앙 보드 상태를 조회
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findByGameId(id)
                .orElseThrow(() -> new IllegalStateException("Central board state not found"));

        Integer selectedCardValue = null;

        if (isFromDeck) {
            // 더미에서 자원 카드 무작위로 가져오기
            if (!centralBoard.getResourceDeck().isEmpty()) {
                // 더미의 맨 위에 있는 자원 카드를 가져옵니다.
                selectedCardValue = centralBoard.getResourceDeck().remove(0);
            } else {
                throw new IllegalStateException("No resource card available in the deck");
            }
        } else {
            // 중앙의 오픈된 자원 카드 중에서 선택
            boolean cardTaken = centralBoard.getResourceCards().removeIf(card -> card.equals(cardValue));
            if (!cardTaken) {
                throw new IllegalStateException("The specified resource card is not available in the open cards");
            }
            selectedCardValue = cardValue;
        }

        // 가져온 자원 카드의 수량을 업데이트
        incrementResourceCardCount(gameState, selectedCardValue);

        // 행동 소모
        gameState.setAction(gameState.getAction() - 1);
        gameStateRepository.save(gameState);
        centralBoardStateRepository.save(centralBoard);
    }
    // 자원 카드 보유 수량을 증가시키는 메서드
    private void incrementResourceCardCount(GameStateEntity gameState, int cardValue) {
        switch (cardValue) {
            case 1 -> gameState.setResourceCard1Count(gameState.getResourceCard1Count() + 1);
            case 2 -> gameState.setResourceCard2Count(gameState.getResourceCard2Count() + 1);
            case 3 -> gameState.setResourceCard3Count(gameState.getResourceCard3Count() + 1);
            case 4 -> gameState.setResourceCard4Count(gameState.getResourceCard4Count() + 1);
            case 5 -> gameState.setResourceCard5Count(gameState.getResourceCard5Count() + 1);
            case 6 -> gameState.setResourceCard6Count(gameState.getResourceCard6Count() + 1);
            case 7 -> gameState.setResourceCard7Count(gameState.getResourceCard7Count() + 1);
            case 8 -> gameState.setResourceCard8Count(gameState.getResourceCard8Count() + 1);
            default -> throw new IllegalArgumentException("Invalid resource card value");
        }
    }


    public void takeFunctionCardToGate(Long gameId, Long gameStateId, Long functionCardId, boolean isFromDeck) {
        GameStateEntity gameState = gameStateRepository.findById(gameStateId)
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

//        // 관문에 추가
//        if (gameState.getReadyRevealCard1() == null) {
//            gameState.setReadyRevealCard1(card.getId().intValue());
//        } else {
//            gameState.setReadyRevealCard2(card.getId().intValue());
//        }

        // 행동 소모
        gameState.setAction(gameState.getAction() - 1);
        gameStateRepository.save(gameState);
        centralBoardStateRepository.save(centralBoard);
    }


}
