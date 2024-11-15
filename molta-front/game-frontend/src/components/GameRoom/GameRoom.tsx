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



const GameRoom: React.FC<GameRoomProps> = () => {
    const { gameId } = useParams<{ gameId: string }>(); 
    const [boardState, setBoardState] = useState<BoardState | null>(null);
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [currentPlayer, setCurrentPlayer] = useState<string | null>(null); // 현재 플레이어 닉네임
    const [connected, setConnected] = useState(false);
    const [messages, setMessages] = useState<string[]>([]);

    

    useEffect(() => {
        const fetchBoardState = async () => {
            try {
                const response = await axios.get(`/room/${gameId}/board-state`);
                setBoardState(response.data);
            } catch (error) {
                console.error("중앙 보드 상태 가져오기 실패:", error);
            }
        };
        const fetchPlayers = async () => {
            try {
                const playerResponse = await axios.get(`/room/${gameId}/players`);
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
                'gameId':gameId
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
                console.log('gameId: ', gameId)

                client.subscribe(`/topic/room/${gameId}/player-joined`, (message: any) => {
                    const data = JSON.parse(message.body);
                    if (data.type === 'PLAYER_JOINED') {
                        fetchPlayers();
                    }
                });
                client.subscribe(`/topic/room/${gameId}/player-left`, (message: any) => {
                    const data = JSON.parse(message.body);                
                    if (data.type === 'PLAYER_LEFT') {
                        fetchPlayers();  // 플레이어 목록 갱신
                    } else {
                        console.log('Invalid message type received:', data.type);
                    }
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



    }, [gameId]);

    if (!boardState) {
        return <div>Loading...</div>;
    }

    return (
        <div className="game-room">
            <OpponentArea position="left" playerInfo={players[0] || defaultPlayerInfo} />
            <OpponentArea position="top" playerInfo={players[1] || defaultPlayerInfo} />
            <OpponentArea position="right" playerInfo={players[2] || defaultPlayerInfo} />
            <CentralBoard boardState={boardState} />
            <PlayerArea playerName={currentPlayer} />
        </div>
    );
};

export default GameRoom;
