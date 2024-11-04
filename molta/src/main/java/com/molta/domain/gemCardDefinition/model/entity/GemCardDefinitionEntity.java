package com.molta.domain.gemCardDefinition.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GemCardDefinitionEntity {

    @Id
    @Column
    @Comment("보석카드 고유 ID")
    private String id;

    @Column
    @Comment("보석카드 점수")
    private int score;

    @Column
    @Comment("카드 종류")
    private int cardType;


}
