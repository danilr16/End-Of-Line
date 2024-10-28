// Board.js
import React from 'react';
import GameCard from "../components/GameCard"; // Import the new GameCard component

export default function Board({ gridSize, size, gridRef }) {
    return (
        <div className="board-frame" style={{ width: `${(gridSize / 5) * 5 + 24}%` }}>
            <div
                className="grid-container"
                ref={gridRef}
                style={{
                    gridTemplateColumns: `repeat(${gridSize}, 1fr)`,
                    gridTemplateRows: `repeat(${gridSize}, 1fr)`,
                    gap: `${(5 / gridSize) * 2}%`,
                }}
            >
                {Array.from({ length: Math.floor((gridSize * gridSize)/2) }, (_, index) => (
                    <div key={index} className="drop-zone" style={{ minWidth: `${size}px`, minHeight: `${size}px` }}></div>
                ))}
                <GameCard size={gridSize} iconName = "start_card" hoverable = {false}/>
                {Array.from({ length: Math.floor((gridSize * gridSize)/2) }, (_, index) => (
                    <div key={index} className="drop-zone" style={{ minWidth: `${size}px`, minHeight: `${size}px` }}></div>
                ))}
            </div>
        </div>
    );
}