package com.molta.domain.gameState.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TakeFunctionCardDTO {
    private String gameId;         // 게임 ID 추가
    private String playerId;
    private Long functionCardId; // 기능 카드 ID
    private boolean fromDeck;    // 더미에서 가져오는지 여부
}

