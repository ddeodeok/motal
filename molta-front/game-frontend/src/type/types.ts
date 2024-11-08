export type PlayerInfo = {
    playerId: String;
    playerName: string;
    score: number;
    gateCards: (number | null)[];
    totalResourceCards: number;
    gemCount: number;
    functionCards: number[];
};

// 기본값 설정 (GameRoom 또는 다른 파일에 설정)
export const defaultPlayerInfo: PlayerInfo = {
    playerId: "대기 중",
    playerName: "대기 중",
    score: 0,
    gateCards: [null, null],
    totalResourceCards: 0,
    gemCount: 0,
    functionCards: []
};

export type GameRoomProps = {
    gameId: string;
    resourceDeckCount: number;
    functionDeckCount: number;
    resourceCards: number[];

    
};

export type BoardState = {
    gameId: string;
    boardState: BoardState;
    leftPlayerInfo: PlayerInfo;
    topPlayerInfo: PlayerInfo;
    rightPlayerInfo: PlayerInfo;
    resourceDeckCount: number;
    functionDeckCount: number;
    resourceCards: number[];
    openResourceCards: number[];
    openFunctionCards: number[];
}