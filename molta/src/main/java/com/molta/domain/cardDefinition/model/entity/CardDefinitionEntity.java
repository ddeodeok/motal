package com.molta.domain.cardDefinition.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CardDefinitionEntity {

    @Id
    @Column(nullable = false)
    @Comment("카드 고유 ID")
    private Long id;
    @Column(nullable = false)
    @Comment("카드 타입(자원,기능)")
    private String cardType;

    @Comment("기능카드 타입")
    private String FuctionCardType;

    @Column(nullable = false)
    @Comment("카드이름")
    private String cardName;

    @Comment("필요자원")
    private String requiredResource;

    @Comment("필요자원 코드")
    private String requiredResourceCode;

    @Comment("점수")
    private int score;

    @Comment("획득보석")
    private int gems;

    @Column(nullable = false)
    @Comment("카드 갯수")
    private int numberOfCards;

    @Comment("카드 기능")
    private String cardFunction;


}
