// import React from 'react';
import React, { useEffect, useState } from 'react';
import '../../styles/CentralBoard.css';
import { BoardState } from '../../type/types';
import axios from '../../api/axiosConfig';


type CentralBoardProps = {
    boardState: BoardState | null;
    handleResourceCardClick: (cardId: number, index: number) => void;  // 자원 카드 클릭 시 처리 함수
    handleFunctionCardClick: (cardId: number, index: number) => void;  // 기능 카드 클릭 시 처리 함수
};

const CentralBoard: React.FC<CentralBoardProps> = ({ boardState, handleResourceCardClick, handleFunctionCardClick }) => {
    if (!boardState) {
        return <div>Loading...</div>;
    }
    const { resourceDeckCount, functionDeckCount, openResourceCards, openFunctionCards,
        resourceCards
    } = boardState;

    
// console.log('boardState',boardState)
// console.log("resourceCards:", openResourceCards)

    return (
        <div className="central-board">
            <div className="open-cards">
                <div className="resource-cards">
                    <div className="resource-deck">
                        <img
                            src={`${process.env.PUBLIC_URL}/images/resource-back.jpg`} 
                            alt="자원 카드 덱" className="card-image" />
                        <div className="deck-count">{resourceDeckCount}장</div>
                    </div>
                    {openResourceCards.map((cardId, index) => (
                        <div key={index} 
                        className="resource-deck" 
                        onClick={() => handleResourceCardClick(cardId, index)}>
                            <img src={`${process.env.PUBLIC_URL}/images/resource-${cardId}.jpg`} 
                            alt={`자원 카드 ${index + 1}`} 
                            className="card-image" />
                        </div>
                    ))}
                </div>
                <div className="function-cards">
                    <div className="function-deck">
                        <img src={`${process.env.PUBLIC_URL}/images/gem.jpg`} alt="기능 카드 덱" className="card-image" />
                        <div className="deck-count">{functionDeckCount}장</div>
                    </div>
                    <div className="card">기능 카드 1</div>
                    <div className="card">기능 카드 2</div>
                </div>
            </div>
            {/* 리소스 카드 새로 오픈하기 버튼 */}
            {/* <button onClick={handleRefillResourceCards}>새로 오픈</button> */}
        </div>
    );
};

export default CentralBoard;
