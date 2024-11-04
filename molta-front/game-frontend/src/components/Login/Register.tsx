import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { registerUser } from '../../api/auth';
import '../../styles/Register.css';

const Register: React.FC = () => {
    const [userId, setUserId] = useState('');
    const [password, setPassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [name, setName] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleRegister = async () => {
        try {
            await registerUser(userId, name, password, nickname);
            alert('회원가입이 완료되었습니다!');
            navigate('/'); // 로그인 페이지로 이동
        } catch (err) {
            setError('회원가입에 실패했습니다. 다시 시도해 주세요.');
        }
    };

    const handleCancel = () => {
        navigate('/'); // 로그인 페이지로 돌아감
    };

    return (
        <div className="register-container">
            <h2>회원가입</h2>
            <input 
                type="text" 
                placeholder="아이디" 
                value={userId} 
                onChange={(e) => setUserId(e.target.value)} 
            />
            <input 
                type="text" 
                placeholder="이름" 
                value={name} 
                onChange={(e) => setName(e.target.value)} 
            />
            <input 
                type="password" 
                placeholder="비밀번호" 
                value={password} 
                onChange={(e) => setPassword(e.target.value)} 
            />
            <input 
                type="text" 
                placeholder="닉네임" 
                value={nickname} 
                onChange={(e) => setNickname(e.target.value)} 
            />
            <button onClick={handleRegister}>회원가입</button>
            <button onClick={handleCancel} className="cancel-button">취소</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
};

export default Register;
