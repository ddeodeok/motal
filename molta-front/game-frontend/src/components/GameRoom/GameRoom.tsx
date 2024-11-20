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



const GameRoom: React.FC<GameRoomProps> = ({ gameId: propGameId }) => {
    const { centralBoardId } = useParams<{ centralBoardId: string }>(); 
    const [boardState, setBoardState] = useState<BoardState | null>(null);
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [currentPlayer, setCurrentPlayer] = useState<string | null>(null); // 현재 플레이어 닉네임
    const [connected, setConnected] = useState(false);
    const [playerResourceCards, setPlayerResourceCards] = useState<number[]>([]); // 내 자원 카드 상태

    // 로컬스토리지에서 gameId를 가져옵니다.
    const gameIdFromLocalStorage = localStorage.getItem('gameId');
    console.log('storage',gameIdFromLocalStorage)

    // gameId가 로컬스토리지에 있을 경우, 로컬스토리지 값을 우선 사용하고 없으면 prop에서 전달된 값 사용
    const gameId = gameIdFromLocalStorage 
    const fetchBoardState = async () => {
        try {
            const response = await axios.get(`/room/${centralBoardId}/board-state`);
            setBoardState(response.data);
        } catch (error) {
            console.error("중앙 보드 상태 가져오기 실패:", error);
        }
    };
    

    useEffect(() => {
        const fetchPlayers = async () => {
            try {
                console.log('central:',centralBoardId)
                const playerResponse = await axios.get(`/room/${centralBoardId}/players`);
                const playerList = playerResponse.data;
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
                // console.log('Authorization',localStorage.getItem('token'))
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
                    console.log("Received gameId:", gameId); 
                    localStorage.setItem('gameId',gameId);
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
    }, [centralBoardId]);
    // 자원 카드 클릭 시 처리 함수
    const handleResourceCardClick = (cardId: number, index: number) => {
        console.log("cardID",cardId)
        console.log("gameID",gameId)
        if (currentPlayer) {
            axios.post(`/game/take-resource-card`, {
                centralBoardId: centralBoardId,
                playerId: currentPlayer,
                cardId: cardId,
                index: index,
                isFromDeck: false, // 덱에서 가져오는 것이 아닌 오픈된 카드에서 가져오는 경우
                gameId: gameId,
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(() => {
                // 카드 가져오기 성공 시, 보드 상태 갱신
                fetchBoardState();  // 보드 상태 다시 가져오기
            })
            .catch(error => {
                console.error('자원 카드 가져오기 실패:', error);
            });
        }
    };
       // 기능 카드 클릭 시 처리 함수
    const handleFunctionCardClick = (cardId: number) => {
        if (currentPlayer) {
            axios.post(`/game/${centralBoardId}/place-function-card`, { playerId: currentPlayer, cardId })
                .then(() => {
                    fetchBoardState();  // 보드 상태 갱신
                })
                .catch(error => console.error('기능 카드 관문에 배치 실패:', error));
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
            />
            <PlayerArea 
            playerName={currentPlayer}
            playerResourceCards={playerResourceCards}
            />
        </div>
    );
};

export default GameRoom;
