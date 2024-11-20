import React, { useState, useEffect } from 'react';
import axios from '../../api/axiosConfig';
import { useNavigate } from 'react-router-dom';
import '../../styles/GameSidebar.css';

interface GameSidebarProps {
    centralBoardId: string | undefined;
    setGameId: (gameId: string) => void;
}

const GameSidebar: React.FC<GameSidebarProps> = ({ centralBoardId, setGameId }) => {
    // `isHost`를 `localStorage`에서 가져와 초기화
    const [isHost, setIsHost] = useState<boolean>(() => localStorage.getItem("isHost") === "true");
    const [isReady, setIsReady] = useState(false);
    const navigate = useNavigate();

    // 준비 상태 변경 함수
    const handleReady = () => {
        const playerId = localStorage.getItem("playerId");
        if (!centralBoardId || !playerId) return;

        const newReadyState = !isReady;
        setIsReady(newReadyState);
        axios.post(`/room/set-ready`, null, {
            params: {
                centralBoardId, // gameId 쿼리 파라미터
                playerId, // playerId 쿼리 파라미터
                isReady: newReadyState // isReady 쿼리 파라미터
            }
        })
            .then(() => alert(`준비 상태: ${newReadyState ? '준비 완료' : '준비 해제'}`))
            .catch(error => console.error("준비 상태 변경 오류:", error));
    };

    const handleStartGame = () => {
        console.log(centralBoardId,centralBoardId)
        axios.post(`/room/${centralBoardId}/start`)
            .then((response) => {
                const gameId = response.data.gameId;
                setGameId(gameId);
                localStorage.setItem('gameId',gameId);
                console.log('gameId',gameId)
                alert("게임이 시작되었습니다!")
            })
            .catch(error => console.error("게임 시작 오류:", error));
    };

    const handleLeaveRoom = async () => {
        try {
            const playerId = localStorage.getItem("playerId");
            if (!centralBoardId || !playerId) {
                console.error("gameId 또는 playerId가 누락되었습니다.");
                return;
            }

            await axios.post(`/room/leave`, null, {
                params: { centralBoardId, playerId }
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
