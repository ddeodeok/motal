package com.molta.domain.centralBoardState.repository;


import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CentralBoardStateRepository extends JpaRepository<CentralBoardStateEntity, String> {
    // 특정 게임 ID에 해당하는 중앙 보드 상태를 가져오는 메서드
    Optional<CentralBoardStateEntity> findByGameId(String id);
    Optional<CentralBoardStateEntity> findById(String id);
}
