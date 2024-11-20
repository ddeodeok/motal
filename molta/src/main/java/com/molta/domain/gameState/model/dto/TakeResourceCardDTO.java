package com.molta.domain.gameState.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TakeResourceCardDTO {
    private String centralBoardId;
    private String gameId; // 게임 상태 ID
    private String playerId;
    private int cardId;    // 자원 카드 값 (1-8)
    private boolean fromDeck; // 더미에서 가져오는지 여부
    private int index;
}
