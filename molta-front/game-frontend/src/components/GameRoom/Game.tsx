import React, { useState, useEffect } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import GameSidebar from './GameSidebar';
import GameRoom from './GameRoom';
import '../../styles/Game.css';
// import { PlayerInfo } from '../../type/types';

const Game: React.FC = () => {
    const { centralBoardId } = useParams<{ centralBoardId: string }>();
    const location = useLocation();
    
    // 상태 관리: 게임 시작 전에 gameId는 null
    const [gameId, setGameId] = useState<string | null>(null);
    const { resourceDeckCount, functionDeckCount, resourceCards } = location.state || {};
    // 게임 시작 함수에서 gameId 설정
    const handleGameStart = (newGameId: string) => {
        setGameId(newGameId);  // 게임 시작 시 받은 gameId를 상태에 저장
    };

    return (
        <div className="game-container">
            <h1 className="game-room-title">게임방: {centralBoardId}</h1>
            <div className="game-main-layout">
                    <GameRoom 
                        gameId={gameId || ""}
                        centralBoardId={centralBoardId || ""} 
                        resourceDeckCount={resourceDeckCount}
                        functionDeckCount={functionDeckCount}
                        resourceCards={resourceCards}
                    /> 
                <GameSidebar 
                centralBoardId={centralBoardId}
                setGameId={handleGameStart} 
                /> 
            </div>
        </div>
    );
};

export default Game;
