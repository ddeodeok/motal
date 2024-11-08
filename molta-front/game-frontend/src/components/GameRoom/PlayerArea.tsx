import React from 'react';
import '../../styles/PlayerArea.css';
import { PlayerInfo } from '../../type/types';
type PlayerAreaProps = {
    playerName: string | null;
};

// const PlayerArea: React.FC = () => {
    const PlayerArea: React.FC<PlayerAreaProps> = ({ playerName }) => {
    return (
        <div className="player-area">
            <div className='score-area'>
                <img src={`${process.env.PUBLIC_URL}/images/scroll-background2.png`} 
                alt="점수배경" className="card-image" />
                <div className="score">0</div>
            </div>  
            <div className="resource-cards">내 자원 카드</div>
                <div className="gate-area">
                    <img src={`${process.env.PUBLIC_URL}/images/gate-player.jpg`} 
                        alt="관문" className="card-image" />
                    <div className="card-slot slot1">
                    </div>
                    <div className="card-slot slot2">
                    </div>
            </div>
            <div className="function-cards">내 기능 카드</div>
        </div>
    );
};

export default PlayerArea;
