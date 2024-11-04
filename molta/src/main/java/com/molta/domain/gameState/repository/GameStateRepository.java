package com.molta.domain.gameState.repository;


import com.molta.domain.gameState.model.entity.GameStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameStateRepository extends JpaRepository<GameStateEntity, String> {
    Optional<GameStateEntity> findByGameIdAndPlayerId(String gameId, String playerId);
    List<GameStateEntity> findAllByGameId(String gameId);
}
