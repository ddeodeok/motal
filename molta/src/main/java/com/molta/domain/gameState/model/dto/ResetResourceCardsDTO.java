package com.molta.domain.gameState.model.dto;

// ResetResourceCardsDTO.java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetResourceCardsDTO {
    private String playerId;
    private String gameId;      // 게임 ID
}
