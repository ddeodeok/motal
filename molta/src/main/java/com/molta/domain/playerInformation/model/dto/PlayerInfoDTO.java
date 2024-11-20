package com.molta.domain.playerInformation.model.dto;


import com.molta.domain.gameState.model.entity.GameStateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoDTO {

    private String playerId;
    private int score;
    private int gemCount;
    private int totalResourceCards;
    private List<Integer> functionCards;
    private List<Integer> gateCards;

    // GameStateEntity를 이용한 생성자
    public PlayerInfoDTO(GameStateEntity gameState) {
        this.playerId = gameState.getPlayerId();
        this.score = gameState.getCurrentScore();
        this.gemCount = gameState.getGemCount();
        this.totalResourceCards = gameState.getResourceCard1Count() + gameState.getResourceCard2Count() /* + ... other resource counts */;
        this.functionCards = Arrays.asList(
                gameState.getSixSixEightEight(),
                gameState.getSevenSevenEightEight() /* + other function card counts */
        );
        this.gateCards = Arrays.asList(
                gameState.getReadyRevealCard1(),
                gameState.getReadyRevealCard2()
        );
    }
}
