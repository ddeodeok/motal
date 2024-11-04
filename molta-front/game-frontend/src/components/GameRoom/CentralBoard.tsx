// import React from 'react';
import React, { useEffect, useState } from 'react';
import '../../styles/CentralBoard.css';
import axios from 'axios';

const CentralBoard: React.FC = () => {
    const [resourceDeckCount, setResourceDeckCount] = useState(0);
    const [openResourceCardIds, setOpenResourceCardIds] = useState<number[]>([]);

    useEffect(() => {
        const fetchResourceCards = async () => {
            try {
                const response = await axios.get('/api/central-board/resource-cards'); // API 수정 필요
                setResourceDeckCount(response.data.resourceDeckCount);
                setOpenResourceCardIds(response.data.openResourceCardIds);
            } catch (error) {
                console.error("Error fetching resource cards:", error);
            }
        };

        fetchResourceCards();
    }, []);

    return (
        <div className="central-board">
            <div className="open-cards">
                <div className="resource-cards">
                    <div className="resource-deck">
                        <img src={`${process.env.PUBLIC_URL}/images/resource-back.jpg`} alt="자원 카드 덱" className="card-image" />
                        <div className="deck-count">{resourceDeckCount}장</div>
                    </div>
                    <div className="card">자원 카드 1</div>
                    <div className="card">자원 카드 2</div>
                    <div className="card">자원 카드 3</div>
                    <div className="card">자원 카드 4</div>
                </div>
                <div className="function-cards">
                    <div className="function-deck">기능 카드 덱</div>
                    <div className="card">기능 카드 1</div>
                    <div className="card">기능 카드 2</div>
                </div>
            </div>
        </div>
    );
};

export default CentralBoard;
