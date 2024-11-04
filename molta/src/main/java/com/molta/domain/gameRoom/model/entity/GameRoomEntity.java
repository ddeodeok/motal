package com.molta.domain.gameRoom.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class GameRoomEntity {

    @Id
    @Comment("게임룸 고유 아이디")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long roomId;

    @Column(nullable = false)
    private String gameId; // 게임 고유 아이디

    @ElementCollection
    @CollectionTable(name = "game_room_players", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "player_id")
    private List<Long> playerIds; // 플레이어들의 ID 리스트

    @Column(nullable = false)
    private String gameStatus; // 게임 상태 (예: "waiting", "in_progress", "finished")

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
