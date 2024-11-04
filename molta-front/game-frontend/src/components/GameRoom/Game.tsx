import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import GameSidebar from './GameSidebar';
import GameRoom from './GameRoom';
import CentralBoard from './CentralBoard';
import OpponentArea from './OpponentArea';
import PlayerArea from './PlayerArea';
import axios from '../../api/axiosConfig';
import '../../styles/Game.css';

const Game: React.FC = () => {
    const { gameId } = useParams<{ gameId: string }>();
    const navigate = useNavigate();

    return (
        <div className="game-container">
            <h1 className="game-room-title">게임방: {gameId}</h1>
            <div className="game-main-layout">
                <GameRoom />  {/* 중앙 게임 화면 */}
                <GameSidebar gameId={gameId} />  {/* 오른쪽 사이드바 */}
            </div>
        </div>
    );
};

export default Game;
