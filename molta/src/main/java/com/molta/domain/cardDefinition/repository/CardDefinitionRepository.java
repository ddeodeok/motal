package com.molta.domain.cardDefinition.repository;

import com.molta.domain.cardDefinition.model.entity.CardDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardDefinitionRepository extends JpaRepository<CardDefinitionEntity, Long> {
    List<CardDefinitionEntity> findAllByCardType(String cardType);
}
