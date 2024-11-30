import React from 'react';
import '../../styles/PlayerArea.css';
import { PlayerInfo } from '../../type/types';
type PlayerAreaProps = {
    playerName: string | null;
    playerResourceCards: number[]; // 보유한 자원 카드 리스트
    readyRevealCard1: number | null;
    readyRevealCard2: number | null;
};

// const PlayerArea: React.FC = () => {
    const PlayerArea: React.FC<PlayerAreaProps> = ({ 
        playerName, 
        playerResourceCards, 
        readyRevealCard1, 
        readyRevealCard2  
    }) => {
        console.log('readyRevealCard1',readyRevealCard1)
        const getCardImage = (cardId: number | undefined) => {
            if (cardId === undefined) return undefined; // 카드가 없으면 null 반환
            return `${process.env.PUBLIC_URL}/images/function-${cardId}.jpg`;  // 해당 카드의 이미지 경로
        };
    
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
                      {/* 1번 관문 */}
                    <div className="card-slot slot1">
                        {readyRevealCard1 && (
                            <img
                                src={getCardImage(readyRevealCard1)}
                                alt={`기능 카드 1번: ${readyRevealCard1}`}
                                className="card-image"
                            />
                        )}
                    </div>
                        {/* 2번 관문 */}
                    <div className="card-slot slot2">
                        {readyRevealCard2 && (
                            <img
                                src={getCardImage(readyRevealCard2)}
                                alt={`기능 카드 2번: ${readyRevealCard2}`}
                                className="card-image"
                            />
                        )}
                    </div>
            </div>
            <div className="function-cards">내 기능 카드</div>
        </div>
    );
};

export default PlayerArea;
