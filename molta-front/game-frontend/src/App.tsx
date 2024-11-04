import React, { useEffect, useState} from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login/Login';
import Register from './components/Login/Register';
import RoomList from './components/main/RoomList';
import Game from './components/GameRoom/Game';

const App: React.FC = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('token'));

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
                <Route path="/game/:gameId" element={isAuthenticated ? <Game /> : <Navigate to="/" />} />
            </Routes>
        </Router>
    );
};

export default App;