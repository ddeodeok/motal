// src/components/RoomList.tsx

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/RoomList.css';
import axios from '../../api/axiosConfig';

interface Room {
    id: number;
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

    // 방 목록 가져오기
    useEffect(() => {
        fetchRooms();
    }, []);

    const fetchRooms = async () => {
        // TODO: 방 목록 가져오기 API 호출
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
        console.log("room", playerIdString, newRoomName,nicknameString);
        const response = await axios.post('/room/create', {
            gameId: newRoomName,
            playerId: playerIdString,
            nickname: nicknameString
        });
        setShowModal(false); // 모달 닫기
        fetchRooms(); // 방 목록 새로 고침
        alert("방이 성공적으로 생성되었습니다!");
        
        // 방 생성 후 해당 방의 페이지로 이동
        navigate(`/game/${response.data}`); // response.data에 생성된 방 ID가 포함되어 있다고 가정
        } catch (error) {
            console.error(error);
            alert("방 생성에 실패했습니다.");
        }
    };

    const handleJoinRoom = async (roomId: number) => {
    try {
            const playerId = localStorage.getItem("playerId"); // playerId 가져오기
            await axios.post('/room/join', null, {
                params: {
                    gameId: roomId,
                    playerId: playerId
                }
            });
            alert("방에 참가하였습니다!");
            navigate(`/game/${roomId}`); // 게임 화면으로 이동
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
            <ul className="room-list">
                {rooms.map(room => (
                    <li key={room.id} className="room-item">
                        <span>{room.name}</span>
                        <button onClick={() => handleJoinRoom(room.id)} className="join-room-button">참가</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default RoomList;
