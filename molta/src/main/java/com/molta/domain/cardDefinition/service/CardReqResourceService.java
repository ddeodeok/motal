package com.molta.domain.cardDefinition.service;

import com.molta.domain.cardDefinition.model.entity.CardDefinitionEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardReqResourceService {


    public boolean checkRequiredResources(List<Integer> submittedCards, String resourceCode, CardDefinitionEntity card) {

        return switch (resourceCode) {
            case "VN" -> checkVN(submittedCards, card);
            case "SNF" -> checkSNF(submittedCards);
            case "SNT" -> checkSNT(submittedCards);
            case "SNTH" -> checkSNTH(submittedCards);
            case "THTAG" -> checkTHTAG(submittedCards);
            case "STHCTW" -> checkSTHCTW(submittedCards);
            case "ODD" -> checkODD(submittedCards);
            case "EVEN" -> checkEVEN(submittedCards);
            case "TSASNT" -> checkTSASNT(submittedCards);
            case "STEN" -> checkSTEN(submittedCards);
            case "STHCTEN" -> checkSTHCTEN(submittedCards);
            case "SFC" -> checkSFC(submittedCards);
            case "STHC" -> checkSTHC(submittedCards);
            case "TVNF" -> checkTVNF(submittedCards);
            case "TVNTH" -> checkTVNTH(submittedCards);
            case "STHCS" -> checkSTHCS(submittedCards);
            case "STAST" -> checkSTAST(submittedCards);
            default -> throw new IllegalArgumentException("Unknown required resource code");
        };
    }

    // 각 조건에 대한 예시 함수
    private boolean checkVN(List<Integer> submittedCards, CardDefinitionEntity card) {
        String requiredResources = card.getRequiredResource(); // 예: "8888" 또는 "18"

        // 필요한 자원 카드를 Map으로 만들어서 각 자원의 개수를 저장 (예: 8 -> 4장)
        Map<Integer, Long> requiredMap = requiredResources.chars()
                .mapToObj(Character::getNumericValue)
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()));
        // 제출된 자원 카드도 같은 방식으로 Map으로 만들어서 각 자원의 개수를 저장
        Map<Integer, Long> submittedMap = submittedCards.stream()
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()));
        // 필요한 자원과 제출된 자원의 개수가 일치하는지 확인
        for (Map.Entry<Integer, Long> entry : requiredMap.entrySet()) {
            int resourceValue = entry.getKey();
            long requiredCount = entry.getValue();
            // 제출된 자원 카드 중 해당 자원의 개수가 부족하면 false 반환
            if (submittedMap.getOrDefault(resourceValue, 0L) < requiredCount) {
                return false;
            }
        }
        return true;
    }

    private boolean checkSNF(List<Integer> submittedCards) {
        // 제출된 카드가 4장이 아니면 SNF 조건을 만족할 수 없음
        if (submittedCards.size() != 4) {
            return false;
        }
        // 첫 번째 카드 값과 나머지 카드 값이 모두 동일한지 검사
        int firstCard = submittedCards.get(0);
        return submittedCards.stream().allMatch(card -> card == firstCard);
    }

    private boolean checkSNT(List<Integer> submittedCards) {
        if (submittedCards.size() < 2) {
            return false;
        }
        int firstCard = submittedCards.get(0);
        return submittedCards.stream().allMatch(card -> card == firstCard);
    }

    private boolean checkSNTH(List<Integer> submittedCards) {
        if (submittedCards.size() < 3) {
            return false;
        }
        int firstCard = submittedCards.get(0);
        return submittedCards.stream().allMatch(card -> card == firstCard);
    }

    private boolean checkTHTAG(List<Integer> submittedCards) {
        if (submittedCards.size() != 4) {
            return false;
        }
        long countOfTwos = submittedCards.stream().filter(card -> card == 2).count();
        long countOfGems = submittedCards.stream().filter(card -> card == -1).count(); // 보석을 -1로 표시한다고 가정
        return countOfTwos == 3 && countOfGems == 1;
    }

    private boolean checkSTHCTW(List<Integer> submittedCards) {
        if (submittedCards.size() != 3) {
            return false;
        }
        int sum = submittedCards.stream().mapToInt(Integer::intValue).sum();
        return sum == 20;
    }

    private boolean checkODD(List<Integer> submittedCards) {
        if (submittedCards.size() != 3) {
            return false;
        }
        return submittedCards.stream().allMatch(card -> card % 2 != 0);
    }

    private boolean checkEVEN(List<Integer> submittedCards) {
        if (submittedCards.size() != 3) {
            return false;
        }
        return submittedCards.stream().allMatch(card -> card % 2 == 0);
    }

    private boolean checkTSASNT(List<Integer> submittedCards) {
        if (submittedCards.size() != 4) {
            return false;
        }
        // 각 숫자의 개수를 세기
        Map<Integer, Long> countMap = submittedCards.stream()
                .collect(Collectors.groupingBy(card -> card, Collectors.counting()));
        // 숫자 6이 2개 있고, 나머지 두 개의 숫자가 동일한지 확인
        boolean hasTwoSixes = countMap.getOrDefault(6, 0L) == 2;
        boolean hasAnotherPair = countMap.values().stream().filter(count -> count == 2).count() == 1;
        boolean condition1 = hasTwoSixes && hasAnotherPair;
        boolean condition2 = countMap.containsKey(6) && countMap.get(6) == 4;
        return condition1 || condition2;
    }

    private boolean checkSTEN(List<Integer> submittedCards) {
        int sum = submittedCards.stream().mapToInt(Integer::intValue).sum();
        return sum == 10;
    }

    private boolean checkSTHCTEN(List<Integer> submittedCards) {
        if (submittedCards.size() != 3) {
            return false;
        }
        int sum = submittedCards.stream().mapToInt(Integer::intValue).sum();
        return sum == 10;
    }

    private boolean checkSFC(List<Integer> submittedCards) {
        if (submittedCards.size() != 5) {
            return false;
        }
        List<Integer> sortedCards = submittedCards.stream().sorted().collect(Collectors.toList());
        // 연속된 숫자인지 확인
        for (int i = 0; i < 4; i++) {
            if (sortedCards.get(i) + 1 != sortedCards.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkSTHC(List<Integer> submittedCards) {
        if (submittedCards.size() != 3) {
            return false;
        }
        List<Integer> sortedCards = submittedCards.stream().sorted().collect(Collectors.toList());
        return (sortedCards.get(0) + 1 == sortedCards.get(1)) &&
                (sortedCards.get(1) + 1 == sortedCards.get(2));
    }
    private boolean checkTVNF(List<Integer> submittedCards) {
        if (submittedCards.size() != 3) {
            return false;
        }
        return submittedCards.stream().allMatch(card -> card == 4) ||
                submittedCards.stream().allMatch(card -> card == 5);
    }
    private boolean checkTVNTH(List<Integer> submittedCards) {
        if (submittedCards.size() != 3) {
            return false;
        }
        return submittedCards.stream().allMatch(card -> card == 3) ||
                submittedCards.stream().allMatch(card -> card == 6);
    }
    private boolean checkSTHCS(List<Integer> submittedCards) {
        if (submittedCards.size() != 3) {
            return false;
        }
        int sum = submittedCards.stream().mapToInt(Integer::intValue).sum();
        return sum == 7;
    }

    private boolean checkSTAST(List<Integer> submittedCards) {
        // 제출된 카드가 4개가 아니면 조건을 만족할 수 없음
        if (submittedCards.size() != 4) {
            return false;
        }
        // 각 숫자의 개수를 세기
        Map<Integer, Long> countMap = submittedCards.stream()
                .collect(Collectors.groupingBy(card -> card, Collectors.counting()));
        boolean condition1 = countMap.size() == 2 && countMap.values().stream().allMatch(count -> count == 2);
        boolean condition2 = countMap.size() == 1 && countMap.values().stream().allMatch(count -> count == 4);
        return condition1 || condition2;
    }
}
