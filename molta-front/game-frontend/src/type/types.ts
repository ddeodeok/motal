export type PlayerInfo = {
    playerId: String;
    playerName: string;
    score: number;
    gateCards: (number | null)[];
    totalResourceCards: number[];
    gemCount: number[];
    functionCards: number[];
};

export interface PlayerState {
    resourceCards: number[]; 
    currentScore: number;    
    functionCards: number[]; 
    readyRevealCard1: number | null; 
    readyRevealCard2: number | null; 
    action: number;          
    maxResourceCardCount: number;  
    currentPlayer: string;    
    playerId: String;
}

export type OpponentPlayerData = {
    playerId: String;
    action: number;
    functionCards: number[];
    maxResourceCardCount: number;
    readyRevealCard1: number | null;
    readyRevealCard2: number | null;
    resourceCards: number[];
    currentScore: number;
    
}

// 기본값 설정 (GameRoom 또는 다른 파일에 설정)
export const defaultPlayerInfo: PlayerInfo = {
    playerId: "대기 중",
    playerName: "대기 중",
    score: 0,
    gateCards: [null, null],
    totalResourceCards: [],
    gemCount: [],
    functionCards: []
};

export type GameRoomProps = {
    gameId: String;
    centralBoardId: string;
    resourceDeckCount: number;
    functionDeckCount: number;
    resourceCards: number[];    
};

export type BoardState = {
    gameId: string | null;
    boardState: BoardState;
    leftPlayerInfo: PlayerInfo;
    topPlayerInfo: PlayerInfo;
    rightPlayerInfo: PlayerInfo;
    resourceDeckCount: number;
    functionDeckCount: number;
    resourceCards: number[];
    openResourceCards: number[];
    openFunctionCards: number[];
    creatorPlayerId: string;
    currentPlayer: string;
}

export type CentralBoardProps = {
    gameId: string;  // 게임 ID 추가
    boardState: BoardState | null;  // 중앙 보드 상태
    handleResourceCardClick: (cardId: number, index:number) => void;  // 자원 카드 클릭 시 처리 함수
    handleFunctionCardClick: (cardId: number, index:number) => void;  // 기능 카드 클릭 시 처리 함수
    handleRefillResourceCards: () => void;  // 리소스 카드 새로 오픈하기
};