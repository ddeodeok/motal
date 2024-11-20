package com.molta.config;

public class CurrentPlayerUpdateMessage {
    private String gameId;
    private String playerId;

    // 기본 생성자
    public CurrentPlayerUpdateMessage() {}

    // 생성자
    public CurrentPlayerUpdateMessage(String gameId, String playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    // Getter and Setter
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}