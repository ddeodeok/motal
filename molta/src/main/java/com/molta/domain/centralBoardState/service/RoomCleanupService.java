package com.molta.domain.centralBoardState.service;

import com.molta.config.WebSocketController;
import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomCleanupService {

    private final CentralBoardStateRepository centralBoardStateRepository;
    @Autowired
    private WebSocketController webSocketController;

    public RoomCleanupService(CentralBoardStateRepository centralBoardStateRepository) {
        this.centralBoardStateRepository = centralBoardStateRepository;
    }
    // 매 10분마다 비활성화된 방을 삭제하는 작업 수행
    @Scheduled(fixedRate = 10 * 60 * 1000) // 10분 간격
    @Transactional
    public void removeInactiveRooms() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(120);

        List<CentralBoardStateEntity> inactiveRooms = centralBoardStateRepository.findByLastActivityTimeBefore(thirtyMinutesAgo);

        for (CentralBoardStateEntity room : inactiveRooms) {
            centralBoardStateRepository.delete(room);
            System.out.println("Deleted room with ID: " + room.getId());
        }
        webSocketController.sendRoomUpdates();
    }


}
