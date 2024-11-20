package com.molta.domain.centralBoardState.model.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomDTO {
    private String centralBoardStateId;

    private String roomName;
    private String nickname;

    private String playerId;

    private String gameId;

    private int resourceDeckCount;

    private List<Integer> resourceCards;

    private int functionDeckCount;



}
