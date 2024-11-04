package com.molta.domain.gemCardDefinition.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.molta.domain.gemCardDefinition.model.entity.GemCardDefinitionEntity;

@Repository
public interface GemCardDefinitionRepository extends JpaRepository<GemCardDefinitionEntity, Long> {
    // 추가적인 쿼리 메서드를 여기서 정의할 수 있습니다.
}
