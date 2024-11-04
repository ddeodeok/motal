package com.molta.domain.centralBoardState.model.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomDTO {
    private String nickname;

    private String playerId;

    private String gameId;
}
