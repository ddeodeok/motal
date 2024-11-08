import React, {useState, useEffect} from 'react';
import { useParams, useLocation  } from 'react-router-dom';
import CentralBoard from './CentralBoard';
import PlayerArea from './PlayerArea';
import OpponentArea from './OpponentArea';
import '../../styles/GameRoom.css';
import { GameRoomProps, BoardState, PlayerInfo, defaultPlayerInfo } from '../../type/types';
import axios from '../../api/axiosConfig';



const GameRoom: React.FC<GameRoomProps> = () => {
    const { gameId } = useParams<{ gameId: string }>(); 
    const [boardState, setBoardState] = useState<BoardState | null>(null);
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [currentPlayer, setCurrentPlayer] = useState<string | null>(null); // 현재 플레이어 닉네임

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
                console.log("localStorage",localStorage)
                const otherPlayers = playerList.filter((p: string) => p !== playerId);
                const player = playerList.find((p: string) => p === playerId);
                console.log('playerList : ', playerList)
                console.log('playerId : ', playerId)
                setPlayers(otherPlayers.map((id: string) => ({ playerId: id })));
                setCurrentPlayer(player );
                console.log('player : ', player)
                console.log('otherPlayers : ', otherPlayers)
            } catch (error) {
                console.error("플레이어 정보 가져오기 실패:", error);
            }
        };
        fetchBoardState();
        fetchPlayers();
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
