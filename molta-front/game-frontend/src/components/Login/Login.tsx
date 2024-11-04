import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser } from '../../api/auth';
import '../../styles/Login.css';


type LoginProps = {
    onLogin: () => void;
};

const Login: React.FC<LoginProps> = ({onLogin}) => {
    const [userId, setUserId] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            const response = await loginUser(userId, password);
            localStorage.setItem('token', response.data.token); // JWT 토큰 저장
            localStorage.setItem('playerId', response.data.playerId); // playerId 저장
            localStorage.setItem('nickname', response.data.nickname); // nickname 저장
            onLogin(); // 로그인 상태 업데이트
            alert('로그인 성공!');
            navigate('/room-list'); // 로그인 성공 시 방 목록 페이지로 이동
        } catch (err) {
            setError('로그인 실패. 다시 시도해 주세요.');
        }
    };

    const goToRegister = () => {
        navigate('/register'); // 회원가입 페이지로 이동
    };

    return (
        <div className="login-container">
            <h2>로그인</h2>
            <input 
                type="text" 
                placeholder="아이디" 
                value={userId} 
                onChange={(e) => setUserId(e.target.value)} 
            />
            <input 
                type="password" 
                placeholder="비밀번호" 
                value={password} 
                onChange={(e) => setPassword(e.target.value)} 
            />
            <button onClick={handleLogin}>로그인</button>
            <button onClick={goToRegister} className="register-button">회원가입</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
};

export default Login;
