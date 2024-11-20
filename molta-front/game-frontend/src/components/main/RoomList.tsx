// src/components/RoomList.tsx

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/RoomList.css';
import axios from '../../api/axiosConfig';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

interface Room {
    gameId: string;
    roomName: string;
    isStarted: boolean;
    hostId: string;
    playerCount: number;
    id: string;
    name: string;
}

type RoomListProps = {
    onLogout: () => void;
};
const RoomList: React.FC<RoomListProps> = ({ onLogout }) =>  {
    const [rooms, setRooms] = useState<Room[]>([]);
    const [showModal, setShowModal] = useState(false);
    const [newRoomName, setNewRoomName] = useState('');
    const navigate = useNavigate();
    const [stompClient, setStompClient] = useState<Client | null>(null);

    // 방 목록 가져오기
    useEffect(() => {
        fetchRooms();
        const socket = new SockJS('http://localhost:8412/ws/chat');
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            connectHeaders: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            onConnect: () => {
                console.log('WebSocket 연결 성공!');
                client.subscribe('/topic/room/updates', (message: any) => {
                    console.log('Room update received:', message.body);
                    fetchRooms();
                });
            },
            onDisconnect: () => {
                console.log('WebSocket 연결 종료');
            },
        });
        setStompClient(client);
        client.activate(); // WebSocket 연결 시작
        return () => {
            if (client) {
                client.deactivate();
            }
        };
    }, []);

    const fetchRooms = async () => {
        try {
            const response = await axios.get('/room/list');  // 방 목록 API 호출
            setRooms(response.data);  // 받아온 방 목록을 rooms 상태에 저장
            
        } catch (error) {
            console.error("방 목록 가져오기 실패:", error);
            // alert("방 목록을 가져오는 데 실패했습니다.");
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        alert("로그아웃되었습니다.");
        navigate('/');
    };


    // 방 생성 함수
    const handleCreateRoom = async () => {
    try {
        const playerIdString = localStorage.getItem("playerId") || ''; // null일 경우 빈 문자열로 대체
        const nicknameString = localStorage.getItem("nickname") || ''; // null일 경우 빈 문자열로 대체
        console.log("room : ", playerIdString, newRoomName,nicknameString);
        const response = await axios.post('/room/create', {
            gameId: newRoomName,
            playerId: playerIdString,
            nickname: nicknameString
        });
        setShowModal(false); // 모달 닫기
        fetchRooms(); // 방 목록 새로 고침
        alert("방이 성공적으로 생성되었습니다!");

         // 응답 데이터에서 필요한 정보 추출
        const { gameId, centralBoardStateId, resourceDeckCount, 
            functionDeckCount, resourceCards } = response.data;
            console.log('response:',response.data)
            console.log('resourceCards:',resourceCards)
        // 방장이므로 로컬스토리지에 방장 정보 저장
        localStorage.setItem("isHost", "true");
        
        // 방 생성 후 해당 방의 페이지로 이동
        navigate(`/game/${centralBoardStateId}`, {
            state: {
                resourceDeckCount,
                functionDeckCount,
                resourceCards
            }
        }); // response.data에 생성된 방 ID가 포함되어 있다고 가정
        } catch (error) {
            console.error(error);
            alert("방 생성에 실패했습니다.");
        }
    };

    const handleJoinRoom = async (centralBoardId: String) => {
    try {
            const playerId = localStorage.getItem("playerId"); // playerId 가져오기
            console.log('centralBoardId',centralBoardId)
            await axios.post('/room/join', null, {
                params: {
                    centralBoardId,
                    playerId
                }
            });
            const boardStateResponse = await axios.get(`/room/${centralBoardId}/board-state`);
            alert("방에 참가하였습니다!");
            // 로컬 스토리지에서 호스트 상태를 false로 설정
            localStorage.setItem("isHost", "false");
            navigate(`/game/${centralBoardId}`, {
                state: {
                    boardState: boardStateResponse.data
                }
            });
        } catch (error) {
            console.error(error);
            alert("방 참가에 실패했습니다.");
        }
    };


    return (
        <div className="room-list-container">
            <div className="top-right">
                <button onClick={onLogout}>로그아웃</button>
            </div>
            <h2>방 목록</h2>
            <button onClick={() => setShowModal(true)} className="create-room-button">방 생성</button>

            {/* 방 생성 모달 */}
            {showModal && (
                <div className="modal">
                    <div className="modal-content">
                        <h3>방 이름을 입력하세요</h3>
                        <input
                            type="text"
                            value={newRoomName}
                            onChange={(e) => setNewRoomName(e.target.value)}
                            placeholder="방 이름"
                        />
                        <button onClick={handleCreateRoom}>생성</button>
                        <button onClick={() => setShowModal(false)}>취소</button>
                    </div>
                </div>
            )}

            {/* 방 목록 */}
            <table className="room-list-table">
                <thead>
                    <tr>
                        <th>순번</th>
                        <th>방 이름</th>
                        <th>플레이어 수</th>
                        <th>방장 아이디</th>
                        <th>게임 상태</th>
                        <th>참가</th>
                    </tr>
                </thead>
                <tbody>
                    {rooms.map((room, index) => (
                        <tr key={room.id}>
                            <td>{index + 1}</td>
                            <td>{room.roomName}</td>
                            <td>{room.playerCount}</td>
                            <td>{room.hostId}</td>
                            <td>{room.isStarted ? '게임 중' : '대기 중'}</td>
                            <td>
                                <button onClick={() => handleJoinRoom(room.id)}>
                                    참가
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default RoomList;
