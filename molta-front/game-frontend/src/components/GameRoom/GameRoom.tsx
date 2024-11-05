import React from 'react';
import { useParams, useLocation  } from 'react-router-dom';
import CentralBoard from './CentralBoard';
import PlayerArea from './PlayerArea';
import OpponentArea from './OpponentArea';
import GameSidebar from './GameSidebar';
import '../../styles/GameRoom.css';

type GameRoomProps = {
    gameId: string;
    resourceDeckCount: number;
    functionDeckCount: number;
    resourceCards: number[];
};

const GameRoom: React.FC<GameRoomProps> = ({ resourceDeckCount, functionDeckCount, resourceCards }) => {
    const { gameId } = useParams<{ gameId: string }>(); 
    return (
        <div className="game-room">
            <OpponentArea position="left" />
            <OpponentArea position="top" />
            <OpponentArea position="right" />
            <CentralBoard gameId={gameId as string}
            resourceDeckCount={resourceDeckCount}
            functionDeckCount={functionDeckCount}
            resourceCards={resourceCards}
            />
            <PlayerArea />
        </div>
    );
};

export default GameRoom;
