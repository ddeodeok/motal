package com.molta.domain.playerInformation.repositiory;


import com.molta.domain.playerInformation.model.entity.PlayerInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerInformationRepository extends JpaRepository<PlayerInformation, Long> {
    Optional<PlayerInformation> findByUserId(String userId);
    Optional<PlayerInformation> findByNickname(String nickName);
}
