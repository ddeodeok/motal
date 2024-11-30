import React, {useState, useEffect, useRef} from 'react';
import { useParams, useLocation  } from 'react-router-dom';
import CentralBoard from './CentralBoard';
import PlayerArea from './PlayerArea';
import OpponentArea from './OpponentArea';
import '../../styles/GameRoom.css';
import { GameRoomProps, BoardState, PlayerInfo, defaultPlayerInfo } from '../../type/types';
import axios from '../../api/axiosConfig';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';



const GameRoom: React.FC<GameRoomProps> = ({ gameId }) => {
    const { centralBoardId } = useParams<{ centralBoardId: string }>(); 
    const [boardState, setBoardState] = useState<BoardState | null>(null);
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [currentPlayer, setCurrentPlayer] = useState<string | null>(null); // 현재 플레이어 닉네임
    const [connected, setConnected] = useState(false);
    const [playerResourceCards, setPlayerResourceCards] = useState<number[]>([]); // 내 자원 카드 상태// 상태 정의
    const [modalOpen, setModalOpen] = useState(false);
    const [cardToDiscard, setCardToDiscard] = useState<number | null>(null);  // 선택된 카드 ID를 저장
    const [cardIndex, setCardIndex] = useState<number| null>(null);
    const [readyRevealCard1, setReadyRevealCard1] = useState<number | null>(null);
    const [readyRevealCard2, setReadyRevealCard2] = useState<number | null>(null);

    // 카드 선택 함수
    const handleCardSelect = (cardId: number) => {
        setCardToDiscard(cardId);  // 선택된 카드를 상태에 저장
    };
    // 모달 열기/닫기 함수
    const openModal = () => {
        setModalOpen(true);  // 모달을 엽니다
    };
    const closeModal = () => {
        setModalOpen(false);  // 모달을 닫습니다
    };


    const fetchBoardState = async () => {
        try {
            const response = await axios.get(`/room/${centralBoardId}/board-state`);
            setBoardState(response.data);
        } catch (error) {
            console.error("중앙 보드 상태 가져오기 실패:", error);
        }
    };

    const fetchPlayerResourceCards = async (playerId: string) => {
        const gameId = localStorage.getItem('gameId');
        if (!gameId) {
            console.log("게임 ID가 없으므로 자원 카드 목록을 불러올 수 없습니다.");
            return;
        }
        try {
            const gameId = localStorage.getItem('gameId');
            const response = await axios.get(`/game/${gameId}/player/${playerId}/resource-cards`);
            const gameState = response.data;
            const resourceCards: number[] = gameState.resourceCards;  
            const readyRevealCard1 = gameState.readyRevealCard1;
            const readyRevealCard2 = gameState.readyRevealCard2;
            setPlayerResourceCards(resourceCards);
            setReadyRevealCard1(readyRevealCard1);
            setReadyRevealCard2(readyRevealCard2);
            return gameState 
        } catch (error) {
            console.error("자원 카드 데이터 가져오기 실패:", error);
        }
    };

      // 새로운 게임에 대한 상태 초기화
    const initializeGameState = () => {
        localStorage.removeItem('gameId');  // 이전 게임 정보 삭제
        setPlayerResourceCards([]);  // 자원 카드 목록 초기화
        // setCurrentPlayer(null);  // 현재 플레이어 초기화
    };

    const fetchCurrentPlayer = async () => {
        const gameId = localStorage.getItem('gameId');
        const response = await axios.get(`/game/${gameId}/current-player`);
        return response.data;
    }

     // 자원 카드 새로 고침 함수
    const handleResetResourceCards = async () => {
        const gameId = localStorage.getItem('gameId');
        const playerId = localStorage.getItem('playerId');

        if (gameId && playerId) {
        try {
            const response = await axios.post('/game/reset-central-resource-cards', {
            gameId: gameId,
            playerId: playerId,
            centralBoardId: centralBoardId,
            });

            console.log(response.data);
            // 새로 고침이 성공하면 중앙 보드 상태를 다시 가져옵니다
            fetchBoardState();
        } catch (error) {
            console.error('자원 카드 새로 고침 실패:', error);
        }
        }
    };

    useEffect(() => {
        // initializeGameState();
        if (currentPlayer) {
            fetchPlayerResourceCards(currentPlayer);
        }
        const fetchPlayers = async () => {
            try {
                console.log('central:',centralBoardId)
                const playerResponse = await axios.get(`/room/${centralBoardId}/players`);
                const playerList = playerResponse.data;
                console.log('playerList',playerList)
                 // 각 플레이어 위치에 할당
                setPlayers([
                    playerList[0] || null,
                    playerList[1] || null,
                    playerList[2] || null,
                ]);
                // 현재 플레이어와 상대 플레이어 구분
                const playerId = localStorage.getItem("playerId");
                const otherPlayers = playerList.filter((p: string) => p !== playerId);
                const player = playerList.find((p: string) => p === playerId);
                setPlayers(otherPlayers.map((id: string) => ({ playerId: id })));
                setCurrentPlayer(player );
                console.log('Current player:', player);  // currentPlayer 확인
                console.log('Player ID from localStorage:', playerId);  // playerId 확인
                // if (player) {
                // }
                // setPlayers(playerList);
            } catch (error) {
                console.error("플레이어 정보 가져오기 실패:", error);
            }
        };
        fetchBoardState();
        fetchPlayers();
        

        // WebSocket 연결을 위한 SockJS 객체 생성
        const socket = new SockJS('http://localhost:8412/ws/chat',{
            headers:{
                'centralBoardId':centralBoardId
            }
        }); 

        // STOMP Client 설정
        const client = new Client({
            webSocketFactory: () => socket,  // SockJS를 통해 WebSocket 연결
            reconnectDelay: 5000,
            connectHeaders: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,  // 헤더에 Authorization 토큰 추가
            },
            onConnect: () => {
                setConnected(true);
                console.log('STOMP 연결 성공!');
                 // 주제 구독 (플레이어가 방에 들어왔을 때 알림을 받기 위함)
                
                client.subscribe(`/topic/room/${centralBoardId}/game-start`, (message: any) => {
                    const gameId = message.body;
                    localStorage.setItem('gameId',gameId);
                    console.log("Received gameId:", gameId); 
                })    
                client.subscribe(`/topic/room/${centralBoardId}/player-joined`, (message: any) => {
                    const data = JSON.parse(message.body);
                    if (data.type === 'PLAYER_JOINED') {
                        fetchPlayers();
                    }
                });
                client.subscribe(`/topic/room/${centralBoardId}/player-left`, (message: any) => {
                    const data = JSON.parse(message.body);                
                    if (data.type === 'PLAYER_LEFT') {
                        fetchPlayers();  // 플레이어 목록 갱신
                    } else {
                        console.log('Invalid message type received:', data.type);
                    }
                });
                client.subscribe(`/topic/room/${centralBoardId}/state`, (message: any) =>{
                    console.log("Game state update received:", message);
                    fetchBoardState();
                });
            },
            onDisconnect: () => {
                setConnected(false);
                console.log('STOMP 연결 종료');
            },
            onStompError: (error) => {
                console.error('STOMP 에러:', error);
            },
            
        });
        client.activate();
        return () => {
            if (client) {
                client.deactivate();
            }
        };
    }, [centralBoardId,currentPlayer]);
    // 자원 카드 클릭 시 처리 함수
    const handleResourceCardClick = async (cardId: number, index: number) => {
        const gameIdFromLocalStorage = localStorage.getItem('gameId');
        console.log('Game ID from localStorage:', gameIdFromLocalStorage);
        const currentPlayer = await fetchCurrentPlayer();  
        const playerId = localStorage.getItem('playerId');  // 사용자 ID를 로컬 스토리지에서 가져옵니다
        console.log('현재플레이어 : ', currentPlayer)
        console.log('플레이어 : ', playerId)
        if (currentPlayer !== playerId) {
            console.log('현재 플레이어와 사용자가 일치하지 않습니다. 모달을 열 수 없습니다.');
            return;  // 플레이어가 일치하지 않으면 함수 종료
        }
        if (currentPlayer) {
            // 자원 카드를 5장 이상 보유하고 있는지 확인
            const playerState = await fetchPlayerResourceCards(currentPlayer);  // 현재 플레이어의 상태 가져오기
            if (playerResourceCards.length >= playerState.maxResourceCardCount) {
                // 자원 카드를 5장 이상 보유한 경우 모달 창 띄우기
                setModalOpen(true);
                console.log('모달', modalOpen)
                setCardToDiscard(cardId);  // 버릴 자원 카드 지정
                setCardIndex(index);  
            } else {
            await axios.post(`/game/take-resource-card`, {
                centralBoardId: centralBoardId,
                playerId: currentPlayer,
                cardId: cardId,
                index: index,
                isFromDeck: false, // 덱에서 가져오는 것이 아닌 오픈된 카드에서 가져오는 경우
                gameId: gameIdFromLocalStorage,
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(() => {
                // 카드 가져오기 성공 시, 보드 상태 갱신
                fetchBoardState(); 
            })
            .catch(error => {
                console.error('자원 카드 가져오기 실패:', error);
            });
            await fetchPlayerResourceCards(currentPlayer);
            }
        }
    };
    // 모달에서 자원 카드 선택 후 처리
    const handleCardDiscard = async (discardCardId: number) => {
        // 선택된 자원 카드 제거 및 discardedResourceCards에 추가하는 작업
        const gameIdFromLocalStorage = localStorage.getItem('gameId');
        console.log('선택된 버릴 카드 ID:', discardCardId);
        console.log('중앙 보드에서 선택한 카드 ID:', cardToDiscard);
        console.log('카드 인덱스:', cardIndex);
        try {
            // 서버로 자원 카드 버리기 요청
            await axios.post('/game/discard-resource-card', {
                centralBoardId: centralBoardId,
                playerId: currentPlayer,
                selectedCardId: cardToDiscard,
                discardCardId: discardCardId,
                gameId: gameIdFromLocalStorage,
                isFromDeck: false,
                index: cardIndex
            });
            fetchBoardState(); 
            // currentPlayer가 null이 아닐 때만 fetchPlayerResourceCards 호출
            if (currentPlayer) {
                await fetchPlayerResourceCards(currentPlayer); // 플레이어 자원 카드 정보 갱신
            } else {
                console.error("현재 플레이어가 선택되지 않았습니다.");
            }
            setModalOpen(false);
        } catch (error) {
            console.error('자원 카드 버리기 실패:', error);
        }
    };


       // 기능 카드 클릭 시 처리 함수
    const handleFunctionCardClick = async (cardId: number,index: number) => {
        if (currentPlayer) {
            // 관문에 배치할 카드가 아직 한 군데라도 비어있는지 확인
            const gameId = localStorage.getItem('gameId');
            const response = await axios.get(`/game/${gameId}/player/${currentPlayer}/gate-status`);
            
            const { readyRevealCard1, readyRevealCard2 } = response.data;
            // 비어있는 관문을 찾아 해당 카드 배치
            if (!readyRevealCard1) {
                // 첫 번째 관문에 배치
                axios.post(`/game/take-function-card-to-gate`, {
                    playerId: currentPlayer,
                    functionCardId: cardId,
                    gameId: gameId,
                    gateNumber: 1,
                    index: index,
                }).then(() => {
                    fetchBoardState();  // 보드 상태 갱신
                    fetchPlayerResourceCards(currentPlayer);
                }).catch(error => console.error('기능 카드 관문에 배치 실패:', error));
            } else if (!readyRevealCard2) {
                // 두 번째 관문에 배치
                axios.post(`/game/take-function-card-to-gate`, {
                    playerId: currentPlayer,
                    functionCardId: cardId,
                    gameId: gameId,
                    gateNumber: 2,
                    index: index,
                }).then(() => {
                    fetchBoardState();  // 보드 상태 갱신
                    fetchPlayerResourceCards(currentPlayer);
                }).catch(error => console.error('기능 카드 관문에 배치 실패:', error));
            } else {
                // 두 관문이 다 차 있으면, 알림 또는 처리
                alert("두 개의 관문이 이미 다 차 있습니다.");
            }
        }
    };



    if (!boardState) {
        return <div>Loading...</div>;
    }

    return (
        
        <div className="game-room">
            <OpponentArea position="left" playerInfo={players[0] || defaultPlayerInfo} />
            <OpponentArea position="top" playerInfo={players[1] || defaultPlayerInfo} />
            <OpponentArea position="right" playerInfo={players[2] || defaultPlayerInfo} />
            <CentralBoard 
                boardState={boardState}
                handleResourceCardClick={handleResourceCardClick}
                handleFunctionCardClick={handleFunctionCardClick}
                handleResetResourceCards={handleResetResourceCards}
            />
            <PlayerArea 
                playerName={currentPlayer}
                playerResourceCards={playerResourceCards}
                readyRevealCard1={readyRevealCard1}
                readyRevealCard2={readyRevealCard2}
            />
            <div>
            {modalOpen && (
                <div className="discard-modal">
                    <h3>자원 카드를 버리세요</h3>
                    <div className="card-container">
                        {playerResourceCards.map((card, index) => (
                            <div key={index} onClick={() => handleCardDiscard(card)}>
                                <img
                                    src={`${process.env.PUBLIC_URL}/images/resource-4${card}.jpg`}
                                    alt={`자원 카드 ${index + 1}`}
                                    className="card-image"
                                />
                            </div>
                        ))}
                    </div>
                    <button onClick={() => setModalOpen(false)}>취소</button>
                </div>
            )}
            </div>
        </div>
    );
};

export default GameRoom;
