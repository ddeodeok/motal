package com.molta.domain.centralBoardState.service;

import com.molta.domain.centralBoardState.model.entity.CentralBoardStateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.molta.domain.centralBoardState.repository.CentralBoardStateRepository;
import java.util.Collections;

@Service
public class CentralBoardStateService {
    // 오픈된 자원 카드를 채우는 함수
    public void checkAndRefillOpenResourceCards(CentralBoardStateEntity centralBoard) {
        while (centralBoard.getResourceCards().size() < 4) {
            if (centralBoard.getResourceDeck().isEmpty()) {
                // 더미가 비었으면, 버려진 카드를 섞어서 추가
                if (!centralBoard.getDiscardedResourceCards().isEmpty()) {
                    Collections.shuffle(centralBoard.getDiscardedResourceCards());
                    centralBoard.getResourceDeck().addAll(centralBoard.getDiscardedResourceCards());
                    centralBoard.getDiscardedResourceCards().clear();
                } else {
                    throw new IllegalStateException("No resource cards available to refill");
                }
            }
            // 더미에서 한 장 가져와 오픈된 카드에 추가
            centralBoard.getResourceCards().add(centralBoard.getResourceDeck().remove(0));
        }
    }

    // 오픈된 기능 카드를 채우는 함수
    public void checkAndRefillOpenFunctionCards(CentralBoardStateEntity centralBoard) {
        while (centralBoard.getFunctionCards().size() < 2) {
            if (centralBoard.getFunctionDeck().isEmpty()) {
                // 더미가 비었으면, 버려진 카드를 섞어서 추가
                if (!centralBoard.getDiscardedFunctionCards().isEmpty()) {
                    Collections.shuffle(centralBoard.getDiscardedFunctionCards());
                    centralBoard.getFunctionDeck().addAll(centralBoard.getDiscardedFunctionCards());
                    centralBoard.getDiscardedFunctionCards().clear();
                } else {
                    throw new IllegalStateException("No function cards available to refill");
                }
            }
            // 더미에서 한 장 가져와 오픈된 카드에 추가
            centralBoard.getFunctionCards().add(centralBoard.getFunctionDeck().remove(0));
        }
    }



}
