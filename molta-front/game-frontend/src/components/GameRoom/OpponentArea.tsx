import React, {useEffect} from 'react';
import '../../styles/OpponentArea.css';
import { PlayerInfo, BoardState, OpponentPlayerData } from '../../type/types';

type OpponentAreaProps = {
    position: "left" | "top" | "right";
    playerInfo: PlayerInfo;
    boardState: BoardState;
    currentTurnPlayer: String | null;
    firstPlayer: string | null;
    // playersData: OpponentPlayerData;
};

const OpponentArea: React.FC<OpponentAreaProps> = ( {position, 
    playerInfo, 
    boardState,
    currentTurnPlayer,
    firstPlayer
    }) => {
    const isCreator = playerInfo.playerId === boardState.creatorPlayerId;


    const getCardImage = (cardId: number | undefined) => {
        if (cardId === undefined) return undefined; 
        return `${process.env.PUBLIC_URL}/images/function-${cardId}.jpg`;  
    };

    useEffect(() => {

    })
    const isCurrentTurnPlayer = playerInfo.playerId === currentTurnPlayer;
    console.log('누가 현재',currentTurnPlayer)
    console.log('Opponet playerId',playerInfo.playerId)
    console.log('OpponentfunctionCards', playerInfo)


    return (
        <div className={`opponent-area ${position} ${isCurrentTurnPlayer ? 'yellow-border' : ''}`}>
            <div className='top-area'>
                <div className='gate-cards'>
                {firstPlayer === playerInfo.playerId && (
                        <div className="gateway">
                            <img 
                                src={`${process.env.PUBLIC_URL}/images/first-player.jpg`} 
                                alt="First Player" 
                                className="first-player-image" 
                            />
                        </div>
                )}
                    <img src={`${process.env.PUBLIC_URL}/images/gate-${position}.jpg`} 
                        alt="관문" className="card-image" />
                    <div className="card-slot slot1">
                        {playerInfo.readyRevealCard1 !== null && 
                            <img                                 
                                src={getCardImage(playerInfo.readyRevealCard1)}
                                alt="관문 카드 1" 
                            />
                        }
                    </div>
                    <div className="card-slot slot2">
                        {playerInfo.readyRevealCard2 !== null && 
                            <img 
                                src={getCardImage(playerInfo.readyRevealCard2)}
                                alt="관문 카드 2" 
                            />
                        }
                    </div>
                </div>
                <div className='top-right'>
                    <div className={`profile ${isCreator ? 'red-border' : ''}`}>
                        {playerInfo.playerId}
                    </div>
                    {/* <div className="profile">
                        {isCreator && <span className="creator-label">H</span>}
                        {playerInfo.playerId}
                    </div> */}
                    <div className="score">
                        <img src={`${process.env.PUBLIC_URL}/images/scroll-background2.png`} 
                            alt="점수배경" className="card-image" />
                        <div className='score-area'>{playerInfo.currentScore}</div>
                    </div>
                </div>
            </div>
            <div className='function-cards'>            
            {position === 'left'}
            {position === 'top' }
            {position === 'right'}
                <div className="card-list">
                    {playerInfo.functionCards.length > 0 ? (
                    playerInfo.functionCards.map((card) => (
                        <img 
                            key={card.cardId} 
                            src={`${process.env.PUBLIC_URL}/images/function-${card.cardId}.jpg`} 
                            alt={`기능 카드 ${card.cardId}`} 
                            className="function-card" 
                        />
                    ))
                    ) : (
                        <p>기능 카드 없음</p>  // 카드가 없을 때 표시할 메시지 또는 비어 있는 상태
                    )}
                </div>
            </div>
            <div className='bottom-area'>
                <div className="resource-cards">
                    <img src={`${process.env.PUBLIC_URL}/images/resource-back-side.jpg`} 
                    alt="자원 카드" className="card-image" />
                    <div className='resource-count'>{playerInfo.resourceCards.length}장</div>
                </div>
                <div className="gems">
                <img src={`${process.env.PUBLIC_URL}/images/gem-side.jpg`} 
                    alt="보석 " className="card-image" />
                    <div className='resource-count'>{playerInfo.gemCards.length}개</div>
                </div>
            </div>
        </div>
    );
};
export default OpponentArea;
