package com.molta.domain.gameState.controller;

import com.molta.domain.gameState.model.dto.ResetResourceCardsDTO;
import com.molta.domain.gameState.model.dto.ResourceSubmissionDTO;
import com.molta.domain.gameState.model.dto.TakeFunctionCardDTO;
import com.molta.domain.gameState.model.dto.TakeResourceCardDTO;
import com.molta.domain.gameState.service.PlayerResetCardsAndEffectCardService;
import com.molta.domain.gameState.service.PlayerTakeCardsService;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class PlayerActionController {

    @Autowired
    private PlayerResetCardsAndEffectCardService playerResetCardsAndEffectCardService;
    @Autowired
    private PlayerTakeCardsService playerTakeCardsService;


    @Comment("관문에 있는 카드 발동 하기 위해 카드 선택해서 제출하는 API")
    @PostMapping("/purchase-function-card")
    public ResponseEntity<String> purchaseFunctionCard(@RequestBody ResourceSubmissionDTO submissionDTO) {
        try {
            playerResetCardsAndEffectCardService.purchaseFunctionCardFromGate(
                    submissionDTO.getGameStateId(),
                    submissionDTO.getPlayerId(),
                    submissionDTO.getGateSlot(),
                    submissionDTO.getSubmittedCards() // 제출된 자원 카드 리스트 전달
            );
            return ResponseEntity.ok("Function card purchased successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Comment("자원 가져오기")
    @PostMapping("/take-resource-card")
    public ResponseEntity<String> takeResourceCard(@RequestBody TakeResourceCardDTO request) {
        System.out.println("Received request: " + request);  // 요청 객체 출력
        try {
            playerTakeCardsService.takeResourceCard(
                    request.getCentralBoardId(),
                    request.getGameId(),
                    request.getPlayerId(),
                    request.getCardId(),
                    request.isFromDeck(),
                    request.getIndex()
            );
            return ResponseEntity.ok("Resource card taken successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @Comment("기능카드 관문에 넣기")
    @PostMapping("/take-function-card-to-gate")
    public ResponseEntity<String> takeFunctionCardToGate(@RequestBody TakeFunctionCardDTO request) {
        try {
            playerTakeCardsService.takeFunctionCardToGate(
                    request.getGameId(),
                    request.getPlayerId(),
                    request.getFunctionCardId(),
                    request.isFromDeck()
            );
            return ResponseEntity.ok("Function card added to gate successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Comment("자원카드 넘기기(새로고침)")
    @PostMapping("/reset-central-resource-cards")
    public ResponseEntity<String> resetCentralResourceCards(@RequestBody ResetResourceCardsDTO request) {
        try {
            playerResetCardsAndEffectCardService.resetCentralResourceCards(
                    request.getGameId(),
                    request.getPlayerId()
            );
            return ResponseEntity.ok("Central resource cards reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



}
