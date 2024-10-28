import React from 'react';

const GameCard = ({ size }) => {
    return (
        <div 
            className="game-card" 
            style={{ minWidth: `${size}px`, minHeight: `${size}px` }}
        >
        </div>
    );
};

export default GameCard;