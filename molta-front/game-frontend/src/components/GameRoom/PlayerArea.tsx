import React, {useState, useEffect} from 'react';
import '../../styles/PlayerArea.css';
import { PlayerInfo } from '../../type/types';
import axios from '../../api/axiosConfig';
import {getFinalResourceCards} from '../../utils/ResourceCardUtils';



export interface GameState {
    resourceCards: number[];
    readyRevealCard1: number | null;
    readyRevealCard2: number | null;
    maxResourceCardCount: number;
    functionCards: PlayerFunctionCard[];
    // 기타 필요한 필드들 추가
}
type PlayerAreaProps = {
    playerName: string | null;
    playerResourceCards: number[]; // 보유한 자원 카드 리스트
    readyRevealCard1: number | null;
    readyRevealCard2: number | null;
    fetchPlayerResourceCards: (playerId: string | null) => Promise<GameState | null>; 
};

interface PlayerFunctionCard {
    cardId: number;
    functionCardType: string;
    cardCount: number;
}

const PlayerArea: React.FC<PlayerAreaProps> = ({ 
    playerName, 
    playerResourceCards, 
    readyRevealCard1, 
    readyRevealCard2,
    fetchPlayerResourceCards  
}) => {
    const [selectedFunctionCard, setSelectedFunctionCard] = useState<{ cardId: number; gateNumber: number } | null>(null);
    const [selectedResourceCard, setSelectedResourceCard] = useState<{ cardId: number, index: number }[]>([]);  
    const [modalOpen, setModalOpen] = useState(false); 
    const [functionCards, setFunctionCards] = useState<{cardId:number}[]>([]);
    const [finalSeletedFunctionCards, setFinalSeletedFunctionCards] = useState<{cardId:number, index: number }[]>([])
    const [playerFunctionCards, setPlayerFunctionCards] = useState<PlayerFunctionCard[]>([]);
    const [blueCards, setBlueCards] = useState<PlayerFunctionCard[]>([]);
    const [redCards, setRedCards] = useState<PlayerFunctionCard[]>([]);
    const [greenCards, setGreenCards] = useState<PlayerFunctionCard[]>([]);

    const sortPlayerFunctionCardsByType = (cards: PlayerFunctionCard[]) => {
        const blueCards = cards.filter(card => card.functionCardType === 'blue');
        const redCards = cards.filter(card => card.functionCardType === 'red');
        const greenCards = cards.filter(card => card.functionCardType === 'green');
    
        return { blueCards, redCards, greenCards };
    };

    const getCardImage = (cardId: number | undefined) => {
        if (cardId === undefined) return undefined; 
        return `${process.env.PUBLIC_URL}/images/function-${cardId}.jpg`;  
    };

    const handleFunctionCardClick = (cardId: number, gateNumber: number) => {
        if (selectedFunctionCard?.cardId === cardId) {
            setSelectedFunctionCard(null); 
            setSelectedResourceCard([]);
        } else {
            setSelectedFunctionCard({cardId,gateNumber}); 
            setSelectedResourceCard([]); 
        }
    };
    const handleResourceCardClick = (cardId: number, index: number) => {
        if (selectedFunctionCard) { 
            setSelectedResourceCard(prev => {
                const cardIndex = prev.findIndex(card => card.cardId === cardId && card.index === index); 
                if (cardIndex === -1) {
                    return [...prev, { cardId, index }]; // 카드가 아직 선택되지 않았다면 추가
                } else {
                    return prev.filter(card => card.cardId !== cardId || card.index !== index); // 카드가 이미 선택되었으면 제거
                }
            });
        }
    };
    // 자원 카드 제출 함수
    const handleSubmitResourceCards = async () => {
        if (selectedFunctionCard && selectedResourceCard.length > 0) {
            const playerResourceCards = selectedResourceCard.map(card => card.cardId);
            let finalResourceCards;
            if (finalSeletedFunctionCards) {
                // 기능 카드에 의해 교환되는 자원카드를 반영 // 기능 카드에 대한 데이터 (예: 교환될 자원 카드 정보)    
                finalResourceCards = getFinalResourceCards(playerResourceCards, finalSeletedFunctionCards);  
                console.log('최종제출자원카드: ', finalResourceCards)
                console.log('제출자원카드: ', playerResourceCards)
            } else {
                finalResourceCards = playerResourceCards; 
            }
            // 확인: finalResourceCards가 제대로 할당됐는지 확인
            if (!finalResourceCards) {
                console.error("finalResourceCards가 올바르게 설정되지 않았습니다.");
                return;
            }
            try {
                const gameId = localStorage.getItem('gameId');
                await axios.post('/game/purchase-function-card', {
                    gameId: gameId,
                    playerId: playerName,
                    // functionCardId: selectedFunctionCard,
                    gateSlot: selectedFunctionCard.gateNumber,
                    submittedCards: finalResourceCards,
                    submittedPlayerCards: playerResourceCards, // 선택된 자원 카드 ID들
                    
                });
                // 제출 후 처리: 모달 창 닫기, 상태 초기화 등
                setModalOpen(false);
                setSelectedFunctionCard(null);
                setSelectedResourceCard([]);
                if (playerName !== null) {  // playerName이 null이 아닐 때만 호출
                    await fetchPlayerResourceCards(playerName);
                    handleFetchPlayerData(playerName);
                } else {
                    console.error('playerName이 null입니다.');
                }
            } catch (error) {
                console.error('자원 카드 제출 실패:', error);
            }
        }
    };

    const handleModalFunctionCardClick = (cardId: number, index: number) => {
        console.log('모달 기능창', cardId)
        setFinalSeletedFunctionCards(prev => {
            const cardIndex = prev.findIndex(card => card.cardId === cardId && card.index === index); 
            if (cardIndex === -1) {
                return [...prev, { cardId, index }]; // 카드가 아직 선택되지 않았다면 추가
            } else {
                return prev.filter(card => card.cardId !== cardId || card.index !== index); // 카드가 이미 선택되었으면 제거
            }
        });
    };

    const fetchActiveFunctionCards = async () => {
        try {
            const gameId = localStorage.getItem('gameId');
            const response = await axios.get(`/game/${gameId}/player/${playerName}/active-function-cards`);
            setFunctionCards(response.data); // 활성화된 기능 카드 배열을 상태에 저장
        } catch (error) {
            console.error('활성화된 기능 카드 가져오기 실패:', error);
        }
    };

const handleFetchPlayerData = async (playerName: any) => {
    const gameState = await fetchPlayerResourceCards(playerName);  
    // gameState가 null이 아닌 경우에만 functionCards에 접근
    if (gameState && gameState.functionCards) {
        const playerFunctionCards: PlayerFunctionCard[] = gameState.functionCards;
        const { blueCards, redCards, greenCards } = sortPlayerFunctionCardsByType(playerFunctionCards);
        console.log('Blue Cards:', blueCards);
        console.log('Red Cards:', redCards);
        console.log('Green Cards:', greenCards);
        setBlueCards(blueCards);
        setRedCards(redCards);
        setGreenCards(greenCards);
    } else {
        console.error('gameState is null');
    }
};

    useEffect(() => {
        handleFetchPlayerData(playerName);
        fetchActiveFunctionCards(); // 컴포넌트 로드 시 활성화된 기능 카드 목록 가져오기
    }, [playerName, finalSeletedFunctionCards]);
    

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
                        <div 
                        key={index} 
                        className={`resource-card ${selectedResourceCard.some(card => card.cardId === cardId && card.index === index) ? 'selected' : ''}`}
                        onClick={() => handleResourceCardClick(cardId, index)}
                        >
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
                
                {/* 자원 카드 제출 버튼 */}
                <div className="submit-button-area">
                    <button
                        onClick={() => setModalOpen(true)}
                        disabled={selectedFunctionCard === null }
                    >
                        자원 카드 제출
                    </button>
                </div>
            </div>
                <div className="gate-area">
                    <img src={`${process.env.PUBLIC_URL}/images/gate-player.jpg`} 
                        alt="관문" className="card-image" />
                      {/* 1번 관문 */}
                    <div 
                        className={`card-slot slot1 ${selectedFunctionCard === readyRevealCard1 ? 'active' : ''}`}
                        onClick={() => readyRevealCard1 && handleFunctionCardClick(readyRevealCard1,1)}
                    >   
                        {readyRevealCard1 && (
                            <img
                                src={getCardImage(readyRevealCard1)}
                                alt={`기능 카드 1번: ${readyRevealCard1}`}
                                className={`card-image ${selectedFunctionCard?.cardId === readyRevealCard1 ? 'active' : ''}`}
                            />
                        )}
                    </div>
                        {/* 2번 관문 */}
                    <div 
                        className="card-slot slot2"
                        onClick={() => readyRevealCard2 && handleFunctionCardClick(readyRevealCard2,2)}
                    >
                        {readyRevealCard2 && (
                            <img
                                src={getCardImage(readyRevealCard2)}
                                alt={`기능 카드 2번: ${readyRevealCard2}`}
                                className={`card-image ${selectedFunctionCard?.cardId === readyRevealCard2 ? 'active' : ''}`}
                            />
                        )}
                    </div>
            </div>
            <div className="function-cards">
                {/* Blue cards */}
                <div className="card-category">
                    <div className="card-container">
                        {blueCards.map(card => (
                            <div key={card.cardId} className="card-item">
                                <img
                                    src={`${process.env.PUBLIC_URL}/images/function-${card.cardId}.jpg`}
                                    alt={`Function Card ${card.cardId}`}
                                    className="card-image"
                                />
                            </div>
                        ))}
                    </div>
                </div>

                {/* Red cards */}
                <div className="card-category">
                    <div className="card-container">
                        {redCards.map(card => (
                            <div key={card.cardId} className="card-item">
                                <img
                                    src={`${process.env.PUBLIC_URL}/images/function-${card.cardId}.jpg`}
                                    alt={`Function Card ${card.cardId}`}
                                    className="card-image"
                                />
                            </div>
                        ))}
                    </div>
                </div>

                {/* Green cards */}
                <div className="card-category">
                    <div className="card-container">
                        {greenCards.map(card => (
                            <div key={card.cardId} className="card-item">
                                <img
                                    src={`${process.env.PUBLIC_URL}/images/function-${card.cardId}.jpg`}
                                    alt={`Function Card ${card.cardId}`}
                                    className="card-image"
                                />
                            </div>
                        ))}
                    </div>
                </div>
            </div>












        {modalOpen && (
            <div className="resource-submit-modal">
            <div className="modal-content">
                <div className="left-side">
                {selectedResourceCard.map((card, index) => (
                    <div key={index} className="card-item">
                    <img
                        src={`${process.env.PUBLIC_URL}/images/resource-4${card.cardId}.jpg`}
                        alt={`자원 카드 ${index + 1}`}
                        className="card-image"
                    />
                    </div>
                ))}
                </div>
                <div className="right-side">
                {functionCards.map((card, index) => (
                    <div 
                    key={index} 
                    className={`function-card ${finalSeletedFunctionCards.some(card => card.cardId === card.cardId && card.index === index) ? 'active' : ''}`}
                    onClick={() => handleModalFunctionCardClick(card.cardId,index)}>
                    <img
                        src={`${process.env.PUBLIC_URL}/images/function-${card.cardId}.jpg`}
                        alt={`기능 카드 ${card.cardId}`}
                        className="card-image"
                    />
                    </div>
                ))}
                </div>
            </div>
            <div className="button-area">
                <button onClick={handleSubmitResourceCards}>제출</button>
                <button onClick={() => setModalOpen(false)} className="cancel-button">취소</button>
            </div>
            </div>
        )}
        </div>
    );
};

export default PlayerArea;
