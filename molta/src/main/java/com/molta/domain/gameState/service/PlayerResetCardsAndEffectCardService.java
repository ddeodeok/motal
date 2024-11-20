package com.molta.domain.gameState.service;

import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import com.molta.domain.gameState.model.entity.GameStateEntity;
import com.molta.domain.gameState.repository.GameStateRepository;
import com.molta.domain.cardDefinition.repository.CardDefinitionRepository;
import com.molta.domain.cardDefinition.model.entity.CardDefinitionEntity;
import com.molta.domain.cardDefinition.service.CardReqResourceService;
import com.molta.domain.gameState.service.GameTurnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@Service
public class PlayerResetCardsAndEffectCardService {

    @Autowired
    private GameTurnService gameTurnService;
    @Autowired
    private CardReqResourceService cardReqResourceService;
    @Autowired
    private CentralBoardStateRepository centralBoardStateRepository;
    @Autowired
    private GameStateRepository gameStateRepository;
    @Autowired
    private CardDefinitionRepository cardDefinitionRepository;


    public void resetCentralResourceCards(String gameId, String playerId) {
        // 특정 플레이어의 게임 상태 조회
        GameStateEntity gameState = gameStateRepository.findByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new IllegalArgumentException("Game state not found for the player"));

        if (gameState.getAction() <= 0) {
            throw new IllegalStateException("No actions remaining for this turn");
        }

        // 게임 ID를 사용해 중앙 보드 상태를 조회
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalStateException("Central board state not found"));

        // 기존 자원 카드 버리기
        centralBoard.getDiscardedResourceCards().addAll(centralBoard.getResourceCards());
        centralBoard.getResourceCards().clear();

        // 새로운 자원 카드 오픈
        for (int i = 0; i < 4; i++) {
            if (!centralBoard.getResourceDeck().isEmpty()) {
                centralBoard.getResourceCards().add(centralBoard.getResourceDeck().remove(0));
            } else if (!centralBoard.getDiscardedResourceCards().isEmpty()) {
                centralBoard.shuffleDiscardedIntoDeck();
                centralBoard.getResourceCards().add(centralBoard.getResourceDeck().remove(0));
            }
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

    //관문에 있는 기능 카드 최종 발동 (행동 소모 1)
    public void purchaseFunctionCardFromGate(String gameId, String playerId, int gateSlot, List<Integer> submittedCards) {
        // 게임 상태 조회
        GameStateEntity gameState = gameStateRepository.findByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new IllegalArgumentException("Game state not found"));

        if (gameState.getAction() <= 0) {
            throw new IllegalStateException("No actions remaining for this turn");
        }

        // 관문 슬롯에서 카드 ID 가져오기
        Integer cardId = (gateSlot == 1) ? gameState.getReadyRevealCard1() : gameState.getReadyRevealCard2();
        if (cardId == null) {
            throw new IllegalStateException("No card in the selected gate slot");
        }

        // 카드 정보 조회
        CardDefinitionEntity card = cardDefinitionRepository.findById(Long.valueOf(cardId))
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // 필요한 자원 확인 후 소모
        // 제출된 자원 카드와 비교하여 자원 충족 여부를 확인
        if (!hasRequiredResources(submittedCards, card)) {
            throw new IllegalStateException("Not enough resources to purchase the card");
        }
        // 자원을 소모하고 소모된 자원 카드를 버려진 카드 더미에 추가
        deductSubmittedResources(gameState, submittedCards);
        CentralBoardStateEntity centralBoard = centralBoardStateRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalStateException("Central board state not found"));
        centralBoard.getDiscardedResourceCards().addAll(submittedCards);  // 버려진 자원 카드 더미에 추가

        // 점수 추가
        gameState.setCurrentScore(gameState.getCurrentScore() + card.getScore());

        // 보석 카드가 있는지 확인하고 추가
        if (card.getGems() > 0) {
            CentralBoardStateEntity updatedCentralBoard = centralBoardStateRepository.findById(gameId)
                    .orElseThrow(() -> new IllegalArgumentException("Central board state not found"));

            for (int i = 0; i < card.getGems(); i++) {
                if (updatedCentralBoard.getFunctionDeck().isEmpty()) {
                    // 기능 카드 더미가 비어 있을 경우, 버려진 기능 카드를 섞어서 더미에 추가
                    if (!updatedCentralBoard.getDiscardedFunctionCards().isEmpty()) {
                        Collections.shuffle(updatedCentralBoard.getDiscardedFunctionCards());
                        updatedCentralBoard.getFunctionDeck().addAll(updatedCentralBoard.getDiscardedFunctionCards());
                        updatedCentralBoard.getDiscardedFunctionCards().clear();
                    } else {
                        throw new IllegalStateException("No more gem cards available in the function deck or discarded pile");
                    }
                }
                // 중앙 보드의 기능 카드 더미에서 맨 위의 카드를 가져와 보석 카드로 사용
                Integer gemCardId = updatedCentralBoard.getFunctionDeck().remove(0);
                gameState.setGemCount(gameState.getGemCount() + 1);
            }
            // 수정 후 updatedCentralBoard의 변경된 상태를 저장
            centralBoard.updateLastActivity();
            centralBoardStateRepository.save(updatedCentralBoard);
        }



        // 발동된 카드 반영
        String cardName = card.getCardName(); // 카드 이름을 사용해 필드명 찾기
        incrementFunctionCardCount(gameState, cardName); // 동적으로 필드 업데이트

        // 관문에서 카드 제거
        if (gateSlot == 1) {
            gameState.setReadyRevealCard1(null);
        } else {
            gameState.setReadyRevealCard2(null);
        }

        // 행동 소모
        gameState.setAction(gameState.getAction() - 1);
        gameStateRepository.save(gameState);
        // 턴 종료 체크 및 턴 넘기기
        if (gameState.getAction() <= 0) {
            gameTurnService.endTurn(gameId, playerId);
        }
    }

    // 발동한 기능 카드의 이름을 사용하여 게임 상태의 필드 값을 증가시키는 메서드
    private void incrementFunctionCardCount(GameStateEntity gameState, String cardName) {
        try {
            Field field = GameStateEntity.class.getDeclaredField(cardName);
            field.setAccessible(true);
            int currentValue = (int) field.get(gameState);
            field.set(gameState, currentValue + 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to update function card count", e);
        }
    }

    // 제출된 자원 카드를 차감하는 메서드
    private void deductSubmittedResources(GameStateEntity gameState, List<Integer> submittedCards) {
        for (Integer cardValue : submittedCards) {
            switch (cardValue) {
                case 1 -> gameState.setResourceCard1Count(gameState.getResourceCard1Count() - 1);
                case 2 -> gameState.setResourceCard2Count(gameState.getResourceCard2Count() - 1);
                case 3 -> gameState.setResourceCard3Count(gameState.getResourceCard3Count() - 1);
                case 4 -> gameState.setResourceCard4Count(gameState.getResourceCard4Count() - 1);
                case 5 -> gameState.setResourceCard5Count(gameState.getResourceCard5Count() - 1);
                case 6 -> gameState.setResourceCard6Count(gameState.getResourceCard6Count() - 1);
                case 7 -> gameState.setResourceCard7Count(gameState.getResourceCard7Count() - 1);
                case 8 -> gameState.setResourceCard8Count(gameState.getResourceCard8Count() - 1);
                default -> throw new IllegalArgumentException("Invalid resource card value");
            }
        }
        gameStateRepository.save(gameState); // 업데이트된 게임 상태를 저장
    }

    // 플레이어가 제출한 자원 카드 목록을 기반으로 자원 조건을 검사
    private boolean hasRequiredResources(List<Integer> submittedCards, CardDefinitionEntity card) {
        // 카드의 필요 자원 코드를 가져와 적절한 조건 검사 함수를 호출
        String resourceCode = card.getRequiredResourceCode();
        return cardReqResourceService.checkRequiredResources(submittedCards, resourceCode, card);
    }
}



















