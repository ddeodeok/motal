package com.molta.domain.gameState.service;

import com.molta.config.WebSocketController;
import com.molta.domain.gameState.model.entity.GameStateEntity;
import com.molta.domain.gameState.repository.GameStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class GameTurnService {

    @Autowired
    private GameStateRepository gameStateRepository;
    @Autowired
    private WebSocketController webSocketController;

    private boolean gameEnded = false; // 게임 종료 여부를 추적
    private String playerWhoReached12 = null; // 12점에 도달한 플레이어

    // 턴 종료 메서드
    public void endTurn(String gameId, String playerId) {
        if (gameEnded) {
            throw new IllegalStateException("The game has already ended.");
        }
        // 현재 플레이어의 게임 상태를 가져옴
        GameStateEntity gameState = gameStateRepository.findByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new IllegalArgumentException("Game state not found"));
        // 남은 행동 수를 초기화
        gameState.setAction(3); // 한 턴에 3번의 행동이 가능
        // 게임 종료 조건 체크: 12점 도달 여부
        if (gameState.getCurrentScore() >= 12 && playerWhoReached12 == null) {
            // 첫 번째로 12점 이상 도달한 플레이어 설정
            playerWhoReached12 = playerId;
            markPlayersForFinalTurns(gameId, playerId); // 마지막 턴 설정
        }
        // 게임 종료 체크 (모든 마지막 턴이 완료되었는지 확인)
        if (playerWhoReached12 != null && areAllFinalTurnsComplete(gameId)) {
            gameEnded = true;
            determineWinner(gameId); // 승자 결정
            return;
        }

        // 다음 플레이어로 턴을 넘기기
        passTurnToNextPlayer(gameId, playerId);
        // 게임 상태 저장
        gameStateRepository.save(gameState);
    }

    // 마지막 턴 설정 메서드
    private void markPlayersForFinalTurns(String gameId, String playerWhoReached12Id) {
        List<GameStateEntity> players = gameStateRepository.findAllByGameId(gameId);

        boolean reached12Found = false; // 12점에 도달한 플레이어 이후를 구분하기 위한 플래그

        for (GameStateEntity player : players) {
            if (player.getPlayerId().equals(playerWhoReached12Id)) {
                // 12점에 도달한 플레이어는 1턴만 남음
                player.setFinalTurn(true);
                player.setExtraTurns(1);
                reached12Found = true;
            } else if (reached12Found) {
                // 12점에 도달한 이후의 플레이어들은 2턴 남음
                player.setExtraTurns(2);
            } else {
                // 12점에 도달하기 이전의 플레이어들은 1턴 남음
                player.setExtraTurns(1);
            }

            gameStateRepository.save(player);
        }
    }


    // 모든 마지막 턴이 완료되었는지 확인하는 메서드
    private boolean areAllFinalTurnsComplete(String gameId) {
        return gameStateRepository.findAllByGameId(gameId).stream()
                .allMatch(GameStateEntity::isFinalTurnComplete);
    }

    // 다음 플레이어로 턴을 넘기는 메서드
    private void passTurnToNextPlayer(String gameId, String currentPlayerId) {
        List<GameStateEntity> players = gameStateRepository.findAllByGameId(gameId);

        // 현재 플레이어의 인덱스 찾기
        int currentPlayerIndex = findCurrentPlayerIndex(players, currentPlayerId);

        // 다음 플레이어로 턴을 넘김
        int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
        String nextPlayerId = players.get(nextPlayerIndex).getPlayerId();

        // 다음 플레이어의 게임 상태 업데이트
        GameStateEntity nextPlayerState = gameStateRepository.findByGameIdAndPlayerId(gameId, nextPlayerId)
                .orElseThrow(() -> new IllegalArgumentException("Next player game state not found"));

        // 행동 초기화 (3번의 행동 기회)
        nextPlayerState.setAction(3);

        // 모든 플레이어의 currentPlayer를 nextPlayerId로 설정
        for (GameStateEntity playerState : players) {
            playerState.setCurrentPlayer(nextPlayerId); // 모든 플레이어의 `currentPlayer`를 `nextPlayerId`로 설정
            gameStateRepository.save(playerState); // 상태 저장
        }

        // 최종 턴이 완료되었는지 체크
        if (nextPlayerState.isFinalTurn()) {
            nextPlayerState.setFinalTurnComplete(true);
        }

        gameStateRepository.save(nextPlayerState);
    }

    private void updateCurrentPlayer(String gameId, String nextPlayerId) {
        // 현재 플레이어 정보를 업데이트할 수 있는 방식으로 전달 (예: WebSocket)
        webSocketController.sendCurrentPlayerUpdate(gameId, nextPlayerId);
    }

    // 현재 플레이어의 인덱스를 찾는 메서드
    private int findCurrentPlayerIndex(List<GameStateEntity> players, String currentPlayerId) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerId().equals(currentPlayerId)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Current player not found in the game");
    }

    // 승자 결정 메서드
    private void determineWinner(String gameId) {
        List<GameStateEntity> players = gameStateRepository.findAllByGameId(gameId);
        Optional<GameStateEntity> winner = players.stream()
                .max(Comparator.comparingInt(GameStateEntity::getCurrentScore)
                        .thenComparing(GameStateEntity::getGemCount));

        winner.ifPresentOrElse(
                win -> System.out.println("The winner is player " + win.getPlayerId()),
                () -> System.out.println("The game ended in a draw")
        );
    }
}
