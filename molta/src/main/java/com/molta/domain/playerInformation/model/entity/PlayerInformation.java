package com.molta.domain.playerInformation.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="playerInforamtion")
@EntityListeners(AuditingEntityListener.class)
public class PlayerInformation {

    @Id
    @Comment("사용자 아이디")
    @Column(nullable = false, unique = true)
    private String userId;

    @Comment("비밀번호")
    @Column(nullable = false)
    private String password;

    @Comment("이름")
    @Column(nullable = false)
    private String name;

    @Comment("닉네임")
    @Column(nullable = false, unique = true)
    private String nickname;

    @Comment("가입일")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Comment("개인전 승")
    @Column(nullable = false)
    private int individualWins;

    @Comment("개인전 무")
    @Column(nullable = false)
    private int individualDraws;

    @Comment("개인전 패")
    @Column(nullable = false)
    private int individualLosses;

    @Comment("팀전 승")
    @Column(nullable = false)
    private int teamWins;

    @Comment("팀전 무")
    @Column(nullable = false)
    private int teamDraws;

    @Comment("팀전 패")
    @Column(nullable = false)
    private int teamLosses;


}
