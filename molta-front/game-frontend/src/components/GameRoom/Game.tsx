import React, { useState, useEffect } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import GameSidebar from './GameSidebar';
import GameRoom from './GameRoom';
import '../../styles/Game.css';
// import { PlayerInfo } from '../../type/types';

// const leftPlayerInfo: PlayerInfo = {
//     playerName: "왼쪽 플레이어",
//     score: 0,
//     gateCards: [null, null],
//     totalResourceCards: 10,
//     gemCount: 2,
//     functionCards: [1, 2, 3]
// };

// const topPlayerInfo: PlayerInfo = {
//     playerName: "위쪽 플레이어",
//     score: 0,
//     gateCards: [null, null],
//     totalResourceCards: 10,
//     gemCount: 2,
//     functionCards: [1, 2, 3]
// };

// const rightPlayerInfo: PlayerInfo = {
//     playerName: "오른쪽 플레이어",
//     score: 0,
//     gateCards: [null, null],
//     totalResourceCards: 10,
//     gemCount: 2,
//     functionCards: [1, 2, 3]
// };

const Game: React.FC = () => {
    const { gameId } = useParams<{ gameId: string }>();
    const location = useLocation();
    const { resourceDeckCount, functionDeckCount, resourceCards } = location.state || {};
    return (
        <div className="game-container">
            <h1 className="game-room-title">게임방: {gameId}</h1>
            <div className="game-main-layout">
                <GameRoom 
                    gameId={gameId || ""} // 기본값을 빈 문자열로 지정
                    resourceDeckCount={resourceDeckCount}
                    functionDeckCount={functionDeckCount}
                    resourceCards={resourceCards}
                    // leftPlayerInfo={leftPlayerInfo}
                    // topPlayerInfo={topPlayerInfo}
                    // rightPlayerInfo={rightPlayerInfo}
                />  {/* 중앙 게임 화면 */}
                <GameSidebar gameId={gameId} />  {/* 오른쪽 사이드바 */}
            </div>
        </div>
    );
};

export default Game;
