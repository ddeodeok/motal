package com.molta.domain.centralBoardState.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CentralBoardStateDTO {
    private int resourceDeckCount;
    private List<Integer> openResourceCards;
    private int functionDeckCount;
    private List<Integer> openFunctionCards;
}
