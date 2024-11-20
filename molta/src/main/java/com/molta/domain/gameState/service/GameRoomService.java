package com.molta.domain.gameState.service;

import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import com.molta.domain.gameState.model.entity.GameStateEntity;
import com.molta.domain.gameState.repository.GameStateRepository;
import com.molta.domain.playerInformation.model.dto.PlayerInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameRoomService {
    @Autowired
    private CentralBoardStateRepository centralBoardStateRepository;

    public List<String> getPlayersInRoom(String centralBoardId) {
        CentralBoardStateEntity boardState = centralBoardStateRepository.findById(centralBoardId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));
        return boardState.getPlayers();
    }
}
