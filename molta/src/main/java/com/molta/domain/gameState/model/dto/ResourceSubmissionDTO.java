package com.molta.domain.gameState.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceSubmissionDTO {
    private String gameStateId;
    private String playerId; // 플레이어 ID
    private String functionCardId; // 기능 카드 ID
    private int gateSlot; // 관문 슬롯
    private List<Integer> submittedCards; // 제출된 자원 카드 리스트
    private int submittedGems; // 제출된 보석 개수
}