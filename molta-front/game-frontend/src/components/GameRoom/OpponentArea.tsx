import React from 'react';
import '../../styles/OpponentArea.css';
import { PlayerInfo } from '../../type/types';

type OpponentAreaProps = {
    position: "left" | "top" | "right";
    playerInfo: PlayerInfo;
};

const OpponentArea: React.FC<OpponentAreaProps> = ( {position, playerInfo}) => {

    // console.log(playerInfo)
    return (
        <div className={`opponent-area ${position}`}>
            <div className='top-area'>
                <div className='gate-cards'>
                    <img src={`${process.env.PUBLIC_URL}/images/gate-${position}.jpg`} 
                        alt="관문" className="card-image" />
                    {/* <div className="card-slot slot1">
                        {playerInfo.gateCards[0] !== null && 
                        <img src={`${process.env.PUBLIC_URL}/images/function-${playerInfo.gateCards[0]}.jpg`} 
                        alt="관문 카드 1" />}
                    </div>
                    <div className="card-slot slot2">
                        {playerInfo.gateCards[1] !== null && 
                        <img src={`${process.env.PUBLIC_URL}/images/function-${playerInfo.gateCards[1]}.jpg`} 
                        alt="관문 카드 2" />}
                    </div> */}
                </div>
                <div className='top-right'>
                    <div className="profile">{playerInfo.playerId}</div>
                        <div className="score">
                            <img src={`${process.env.PUBLIC_URL}/images/scroll-background2.png`} 
                                alt="점수배경" className="card-image" />
                            <div className='score-area'>00</div>
                        </div>
                </div>
            </div>
            <div className='function-cards'>            
            {position === 'left' && '왼쪽 플레이어 기능 카드'}
            {position === 'top' && '위쪽 플레이어 기능 카드'}
            {position === 'right' && '오른쪽 플레이어 기능 카드'}
                <div className="card-list">
                    {/* {playerInfo.functionCards.length > 0 ? (
                    playerInfo.functionCards.map((cardId, index) => (
                        <img 
                            key={index} 
                            src={`${process.env.PUBLIC_URL}/images/function-${cardId}.jpg`} 
                            alt={`기능 카드 ${cardId}`} 
                            className="function-card" 
                        />
                    ))
                    ) : (
                        <p>기능 카드 없음</p>  // 카드가 없을 때 표시할 메시지 또는 비어 있는 상태
                    )} */}
                </div>
            </div>
            <div className='bottom-area'>
                <div className="resource-cards">
                    <img src={`${process.env.PUBLIC_URL}/images/resource-back-side.jpg`} 
                    alt="자원 카드" className="card-image" />
                    {/* <div className='resource-count'>{playerInfo.totalResourceCards}장</div> */}
                </div>
                <div className="gems">
                <img src={`${process.env.PUBLIC_URL}/images/gem-side.jpg`} 
                    alt="보석 " className="card-image" />
                    {/* <div className='resource-count'>{playerInfo.gemCount}개</div> */}
                </div>
            </div>
        </div>
    );
};
export default OpponentArea;
