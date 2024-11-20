package com.molta.domain.centralBoardState.model.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private String id;
    private String gameId;
    private String roomName;
    private Boolean isStarted;
    private String hostId;
    private int playerCount;
}
