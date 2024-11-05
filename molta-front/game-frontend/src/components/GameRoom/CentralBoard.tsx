// import React from 'react';
import React, { useEffect, useState } from 'react';
import '../../styles/CentralBoard.css';
import axios from 'axios';

type CentralBoardProps = {
    gameId: string;
    resourceDeckCount: number;
    functionDeckCount: number;
    resourceCards: number[];
};

const CentralBoard: React.FC<CentralBoardProps> = ({ gameId, resourceDeckCount, functionDeckCount, resourceCards }) => {
console.log("resourceCards:", resourceCards)
    return (
        <div className="central-board">
            <div className="open-cards">
                <div className="resource-cards">
                    <div className="resource-deck">
                        <img src={`${process.env.PUBLIC_URL}/images/resource-back.jpg`} alt="자원 카드 덱" className="card-image" />
                        <div className="deck-count">{resourceDeckCount}장</div>
                    </div>
                    {resourceCards.map((cardId, index) => (
                        <div key={index} className="resource-deck">
                            <img src={`${process.env.PUBLIC_URL}/images/resource-${cardId}.PNG`} alt={`자원 카드 ${index + 1}`} className="card-image" />
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
        </div>
    );
};

export default CentralBoard;
