import React from 'react';
import '../../styles/PlayerArea.css';
import { PlayerInfo } from '../../type/types';
type PlayerAreaProps = {
    playerName: string | null;
    playerResourceCards: number[]; // 보유한 자원 카드 리스트
};

// const PlayerArea: React.FC = () => {
    const PlayerArea: React.FC<PlayerAreaProps> = ({ playerName, playerResourceCards }) => {
    return (
        <div className="player-area">
            <div className='score-area'>
                <img src={`${process.env.PUBLIC_URL}/images/scroll-background2.png`} 
                alt="점수배경" className="card-image" />
                <div className="score">0</div>
            </div>  
            <div className="resource-cards">
                {playerResourceCards.length > 0 ? (
                    playerResourceCards.map((cardId, index) => (
                        <div key={index} className="resource-card">
                            <img
                                src={`${process.env.PUBLIC_URL}/images/resource-4${cardId}.jpg`}
                                alt={`자원 카드 ${index + 1}`}
                                className="card-image"
                            />
                        </div>
                    ))
                ) : (
                    <p>자원 카드 없음</p>
                )}
            </div>
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
