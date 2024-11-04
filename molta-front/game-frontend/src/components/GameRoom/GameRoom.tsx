import React from 'react';
import { useParams } from 'react-router-dom';
import CentralBoard from './CentralBoard';
import PlayerArea from './PlayerArea';
import OpponentArea from './OpponentArea';
import GameSidebar from './GameSidebar';
import '../../styles/GameRoom.css';

const GameRoom: React.FC = () => {
    const { gameId } = useParams<{ gameId: string }>(); 
    return (
        <div className="game-room">
            <OpponentArea position="left" />
            <OpponentArea position="top" />
            <OpponentArea position="right" />
            <CentralBoard />
            <PlayerArea />
        </div>
    );
};

export default GameRoom;
