import React from 'react';
import '../../styles/OpponentArea.css';


interface OpponentAreaProps {
    position: 'left' | 'right' | 'top';
}

const OpponentArea: React.FC<OpponentAreaProps> = ({ position }) => {
    return (
        <div className={`opponent-area ${position}`}>
            <div className='top-area'>
                <div className='gate-cards'>관문 카드</div>
                <div className='top-right'>
                    <div className='profile'>프로필</div>
                    <div className='score'>점수</div>
                </div>
            </div>
            <div className='function-cards'>            
            {position === 'left' && '왼쪽 플레이어 기능 카드'}
            {position === 'top' && '위쪽 플레이어 기능 카드'}
            {position === 'right' && '오른쪽 플레이어 기능 카드'}
            </div>
            <div className='bottom-area'>
                <div className='resource-cards'>자원카드</div>
                <div className='gems'>보석</div>
            </div>
        </div>
    );
};
export default OpponentArea;
