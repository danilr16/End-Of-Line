// Board.js
import React from 'react';

export default function Board({ gridSize, gridItemSize, gridRef }) {
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
                {Array.from({ length: gridSize * gridSize }, (_, index) => (
                    <div key={index} className="drop-zone"></div>
                ))}
            </div>
        </div>
    );
}