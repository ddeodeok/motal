import React, { useEffect, useState} from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login/Login';
import Register from './components/Login/Register';
import RoomList from './components/main/RoomList';
import Game from './components/GameRoom/Game';

import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const App: React.FC = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('token'));
    // const [connected, setConnected] = useState(false);
    //        // WebSocket 연결을 위한 SockJS 객체 생성
    //        const socket = new SockJS('http://localhost:8412/ws/chat',{
    //         headers:{

    //         }
    //     }); 

    //     // STOMP Client 설정
    //     const client = new Client({
    //         webSocketFactory: () => socket,  // SockJS를 통해 WebSocket 연결
    //         reconnectDelay: 5000,
    //         connectHeaders: {
    //             'Authorization': `Bearer ${localStorage.getItem('token')}`,  // 헤더에 Authorization 토큰 추가
    //         },
    //         onConnect: () => {
    //             setConnected(true);
    //             console.log('STOMP 연결 성공!');
    //         },
    //         onDisconnect: () => {
    //             setConnected(false);
    //             console.log('STOMP 연결 종료');
    //         },
    //         onStompError: (error) => {
    //             console.error('STOMP 에러:', error);
    //         },
            
    //     });
    //     client.activate();

    const handleLogin = () => {
        setIsAuthenticated(true); // 로그인 상태를 true로 설정
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        setIsAuthenticated(false); // 로그아웃 상태로 전환
    };

    return (
        <Router>
            <Routes>
                <Route path="/" element={isAuthenticated ? <Navigate to="/room-list" /> : <Login onLogin={handleLogin} />} />
                <Route path="/register" element={<Register />} />
                <Route path="/room-list" element={isAuthenticated ? <RoomList onLogout={handleLogout} /> : <Navigate to="/" />} />
                <Route path="/game/:centralBoardId" element={isAuthenticated ? <Game /> : <Navigate to="/" />} />
            </Routes>
        </Router>
    );
};

export default App;