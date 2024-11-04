import React, { useState, useEffect } from 'react';
import axios from '../../api/axiosConfig';
import { useNavigate } from 'react-router-dom';
import '../../styles/GameSidebar.css';

interface GameSidebarProps {
    gameId: string | undefined;
}

const GameSidebar: React.FC<GameSidebarProps> = ({ gameId }) => {
    const [isHost, setIsHost] = useState(false);
    const [isReady, setIsReady] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const playerId = localStorage.getItem("playerId");
        axios.get(`/room/${gameId}/is-host`, { params: { playerId } })
            .then(response => setIsHost(response.data.isHost))
            .catch(error => console.error("방장 여부 확인 오류:", error));
    }, [gameId]);

    const handleReady = () => {
        setIsReady(!isReady);
        axios.post(`/room/${gameId}/set-ready`, { playerId: localStorage.getItem("playerId"), isReady: !isReady })
            .then(() => alert(`준비 상태: ${!isReady ? '준비 완료' : '준비 해제'}`))
            .catch(error => console.error("준비 상태 변경 오류:", error));
    };

    const handleStartGame = () => {
        axios.post(`/room/${gameId}/start`)
            .then(() => alert("게임이 시작되었습니다!"))
            .catch(error => console.error("게임 시작 오류:", error));
    };

    const handleLeaveRoom = async () => {
        try {
            const playerId = localStorage.getItem("playerId");
            if (!gameId || !playerId) {
                console.error("gameId 또는 playerId가 누락되었습니다.");
                return;
            }

            await axios.post(`/room/leave`, null, {
                params: { gameId, playerId }
            });
            alert('방에서 나왔습니다.');
            navigate('/room-list');
        } catch (error) {
            console.error("방 나가기 오류:", error);
            alert("방 나가기에 실패했습니다.");
        }
    };

    return (
        <div className="game-sidebar">
            {isHost ? (
                <button onClick={handleStartGame} className="start-button">시작</button>
            ) : (
                <button onClick={handleReady} className="ready-button">
                    {isReady ? "준비 해제" : "준비"}
                </button>
            )}
            <button onClick={handleLeaveRoom} className="leave-button">나가기</button>
        </div>
    );
};

export default GameSidebar;