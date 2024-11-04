import React from 'react';
import '../../styles/PlayerArea.css';

const PlayerArea: React.FC = () => {
    return (
        <div className="player-area">
            <div className="resource-cards">내 자원 카드</div>
            <div className="gate-area">관문 카드 영역</div>
            <div className="score">점수: 0</div>
            <div className="function-cards">내 기능 카드</div>
        </div>
    );
};

export default PlayerArea;
