package com.molta.domain.gameState.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameStateEntity {

    @Id
    @Comment("고유 아이디")
    @Column(nullable = false)
    private String id;

    @Comment("게임플레이 고유 아이디")
    @Column(nullable = false)
    private Long gameId;

    @Comment("플레이어 아이디")
    @Column(nullable = false)
    private Long playerId;

    @Comment("플레이어 남은행동")
    @Column(nullable = false)
    @Builder.Default
    private int action = 3;

    // 자원 카드 보유 갯수 (1-8)
    @Comment("자원 카드 1 보유 갯수")
    @Column(nullable = false)
    private int resourceCard1Count;

    @Comment("자원 카드 2 보유 갯수")
    @Column(nullable = false)
    private int resourceCard2Count;

    @Comment("자원 카드 3 보유 갯수")
    @Column(nullable = false)
    private int resourceCard3Count;

    @Comment("자원 카드 4 보유 갯수")
    @Column(nullable = false)
    private int resourceCard4Count;

    @Comment("자원 카드 5 보유 갯수")
    @Column(nullable = false)
    private int resourceCard5Count;

    @Comment("자원 카드 6 보유 갯수")
    @Column(nullable = false)
    private int resourceCard6Count;

    @Comment("자원 카드 7 보유 갯수")
    @Column(nullable = false)
    private int resourceCard7Count;

    @Comment("자원 카드 8 보유 갯수")
    @Column(nullable = false)
    private int resourceCard8Count;

    @Comment("자원 카드 최대 보유 갯수")
    @Column(nullable = false)
    @Builder.Default
    private int maxResourceCardCount = 5;

    @Comment("보석 갯수")
    @Column(nullable = false)
    private int gemCount;

    // 기능 카드 보유 갯수 (20종류)
    // 점수카드
    @Comment("6688 보유 갯수")
    @Column(nullable = false)
    private int sixSixEightEight;

    @Comment("7788 보유 갯수")
    @Column(nullable = false)
    private int sevenSevenEightEight;

    @Comment("7777 보유 갯수")
    @Column(nullable = false)
    private int fourSeven;

    @Comment("8888 보유 갯수")
    @Column(nullable = false)
    private int fourEight;

    @Comment("88 보유 갯수")
    @Column(nullable = false)
    private int twoEight;

    @Comment("1234 보유 갯수")
    @Column(nullable = false)
    private int oneTwoThreeFour;

    @Comment("같은거 4장 보유 갯수")
    @Column(nullable = false)
    private int sameFourCards;

    @Comment("같은거 3장 보유 갯수")
    @Column(nullable = false)
    private int sameThreeCards;

    @Comment("같은거 2장 보유 갯수")
    @Column(nullable = false)
    private int sameTwoCards;

    @Comment("222+보석 보유 갯수")
    @Column(nullable = false)
    private int threeTwoAndGem;

    @Comment("3장카드합 20 보유 갯수")
    @Column(nullable = false)
    private int sumThreeCards20;

    @Comment("홀수카드 3장 보유 갯수")
    @Column(nullable = false)
    private int threeOddCards;

    @Comment("짝수카드 3장 보유 갯수")
    @Column(nullable = false)
    private int threeEvenCards;

    @Comment("같은거 2장 + 66 보유 갯수")
    @Column(nullable = false)
    private int twoSixAndSameTwoCard;

    // 파랑기능카드
    @Comment("1 카드")
    @Column(nullable = false)
    private int twoOne;

    @Comment("2카드")
    @Column(nullable = false)
    private int twoTwo;

    @Comment("3 카드")
    @Column(nullable = false)
    private int twoThree;

    @Comment("4 카드")
    @Column(nullable = false)
    private int twoFour;

    @Comment("5 카드")
    @Column(nullable = false)
    private int TwoFive;

    @Comment("6 카드")
    @Column(nullable = false)
    private int TwoSix;

    @Comment("7 카드")
    @Column(nullable = false)
    private int TwoSeven;

    @Comment("8 카드")
    @Column(nullable = false)
    private int oneTwo;

    @Comment("2카드를 보석으로 교환")
    @Column(nullable = false)
    private int two;

    @Comment("만능 숫자 카드")
    @Column(nullable = false)
    private int fourOne;

    @Comment("매턴 행동 1 추가")
    @Column(nullable = false)
    private int fromFourToEight;

    @Comment("카드합 10, 최대보유 수량 +1")
    @Column(nullable = false)
    private int sum10;

    @Comment("3장카드합 10, 1->8")
    @Column(nullable = false)
    private int sumOfThreeCards10;

    @Comment("자원카드 교환")
    @Column(nullable = false)
    private int oneEight;

    @Comment("보석 빼기 기능 추가")
    @Column(nullable = false)
    private int ThreeFiveSeven;

    @Comment("3을 조커로")
    @Column(nullable = false)
    private int threeThree;

    @Comment("계시카드 교환")
    @Column(nullable = false)
    private int straight5Cards;

    @Comment("미리보기(3연속카드)")
    @Column(nullable = false)
    private int straight3Cards;

    // 래드 카드
    @Comment("1357 행동 즉시 3 추가")
    @Column(nullable = false)
    private int oneThreeFiveSeven;

    @Comment("2468 행동 즉시 3 추가")
    @Column(nullable = false)
    private int twoFourSixEight;

    @Comment("상대카드(자원) 하나 가져오기")
    @Column(nullable = false)
    private int FiveSixSeven;

    @Comment("사용카드중 하나 가져오기 랜덤")
    @Column(nullable = false)
    private int ThreeFourFive;

    @Comment("444/555 양옆도 발동 가능")
    @Column(nullable = false)
    private int ThreeFourOrThreeFive;

    @Comment("333/666 양옆도 발동 가능")
    @Column(nullable = false)
    private int ThreeThreeOrThreeSix;

    @Comment("상대 계시 준비 카드 버리기")
    @Column(nullable = false)
    private int SumThreeCards7;

    @Comment("다음플레이어 행동 추가")
    @Column(nullable = false)
    private int sameTwoCardsAndSameTwoCards;


    @Comment("계시 준비 카드 종류 (1)")
    @Column(nullable = true)
    private Integer readyRevealCard1;

    @Comment("계시 준비 카드 종류 (2)")
    @Column(nullable = true)
    private Integer readyRevealCard2;

    @Comment("현재 득점 점수")
    @Column(nullable = false)
    private int currentScore;

    @Comment("보유 액션")
    @Column(nullable = false)
    @Builder.Default
    private int remainingAction = 3;

    @Comment("마지막 업데이트 시간")
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

}
