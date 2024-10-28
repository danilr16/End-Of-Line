import React from 'react';

const GameCard = ({size, setBeingDraggedCard,index }) => {
    const handleDragStart = () => {setBeingDraggedCard(index)}
    const handleDragEnd = () => {setBeingDraggedCard(null)}
    return (
        <div 
            className="game-card" 
            draggable
            onDragStart={handleDragStart}
            onDragEnd={handleDragEnd}
            style={{ minWidth: `${size}px`, minHeight: `${size}px` }}
        >
        </div>
    );
};

export default GameCard;