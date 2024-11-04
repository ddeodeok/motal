
import React, {useState} from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './components/Login/Login';
// import Register from './components/Register';
// import RoomList from './components/RoomList';
// import Game from './components/Game';

const AppRoutes: React.FC = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('token'));

    const handleLogin = () => {
        setIsAuthenticated(true); // 로그인 상태를 업데이트
    };

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login onLogin={handleLogin} />} />
                {/* 다른 경로들 */}
            </Routes>
        </Router>
    );
};

export default AppRoutes;
