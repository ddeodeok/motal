

export const resourceCardReplacementMap: Record<number, number> = {
    15: 1,
    16: 2,
    17: 3,
    18: 4,
    19: 5,
    20: 6,
    21: 7,
    22: 8,
};
export function getFinalResourceCards(selectedResourceCards: number [], 
    finalFunctionCards:{ cardId: number, index: number}[]): number[] {

    let finalResourceCards: number[] = [...selectedResourceCards.map(card => card)];
    finalFunctionCards.forEach(functionCardId => {
        if (resourceCardReplacementMap[functionCardId.cardId]) {
            const resourceToReplace = resourceCardReplacementMap[functionCardId.cardId];
            console.log("Resource to replace: ", resourceToReplace);  
            finalResourceCards.push(resourceToReplace);
        }
    });
    return finalResourceCards;
}