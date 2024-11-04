package com.molta.domain.centralBoardState.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.Comment;

import java.util.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CentralBoardStateEntity {

    @Id
    @Comment("고유 아이디")
    @Column(nullable = false, unique = true)
    private String id = UUID.randomUUID().toString();

    @Comment("게임의 고유 아이디")
    private String gameId;

    @Comment("방이름")
    private String roomName;

    @Comment("방장 아이디")
    private String CreatorPlayerId;

    @ElementCollection
    @CollectionTable(name = "central_board_players", joinColumns = @JoinColumn(name = "central_board_id"))
    @Column(name = "player_id")
    @Builder.Default
    private List<String> players = new ArrayList<>(); // 참가한 플레이어 ID 목록

    @ElementCollection
    @CollectionTable(name = "player_ready_status", joinColumns = @JoinColumn(name = "central_board_id"))
    @MapKeyColumn(name = "player_id")
    @Column(name = "is_ready")
    private Map<String, Boolean> playerReadyStatus = new HashMap<>(); // 플레이어 준비 상태

    @Comment("게임 시작 여부")
    @Column(name = "is_started")
    private boolean isStarted = false;

    @ElementCollection
    @CollectionTable(name = "central_function_cards", joinColumns = @JoinColumn(name = "central_board_id"))
    @Column(name = "function_card")
    @Comment("중앙 보드에 놓인 기능 카드들")
    private List<Integer> functionCards = new ArrayList<>(); // 기능 카드 2장

    @ElementCollection
    @CollectionTable(name = "central_resource_cards", joinColumns = @JoinColumn(name = "central_board_id"))
    @Column(name = "resource_card")
    @Comment("중앙 보드에 놓인 자원 카드들")
    private List<Integer> resourceCards = new ArrayList<>(); // 자원 카드 4장

    @ElementCollection
    @CollectionTable(name = "resource_deck", joinColumns = @JoinColumn(name = "central_board_id"))
    @Column(name = "deck_card")
    @Comment("자원 카드 더미")
    private List<Integer> resourceDeck = new ArrayList<>(); // 자원 카드 더미

    @ElementCollection
    @CollectionTable(name = "discarded_resource_cards", joinColumns = @JoinColumn(name = "central_board_id"))
    @Column(name = "discarded_card")
    @Comment("버려진 자원 카드들")
    private List<Integer> discardedResourceCards = new ArrayList<>(); // 버려진 자원 카드

    @ElementCollection
    @CollectionTable(name = "function_deck", joinColumns = @JoinColumn(name = "central_board_id"))
    @Column(name = "deck_card")
    @Comment("기능 카드 더미")
    private List<Integer> functionDeck = new ArrayList<>(); // 기능 카드 더미

    @ElementCollection
    @CollectionTable(name = "discarded_function_cards", joinColumns = @JoinColumn(name = "central_board_id"))
    @Column(name = "discarded_card")
    @Comment("버려진 기능 카드들")
    private List<Integer> discardedFunctionCards = new ArrayList<>(); // 버려진 기능 카드

    // 버려진 자원 카드들을 더미에 섞어 넣는 메서드
    public void shuffleDiscardedIntoDeck() {
        resourceDeck.addAll(discardedResourceCards);
        discardedResourceCards.clear();
        Collections.shuffle(resourceDeck);
    }
}
