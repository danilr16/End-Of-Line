import React from 'react';
import DropZone from './DropZone';
import "../static/css/components/board.css"

export default function Board({ gridSize, size, gridRef, onDrop, boardItems, isDragging, hoveredIndex, setHoveredIndex, possiblePositions, hoveredRotation, setHoveredRotation }) {
    // Ensure boardItems is a valid array and nested structure is intact
    if (!Array.isArray(boardItems) || boardItems.length === 0 || boardItems.some(row => !Array.isArray(row) || row.length === 0)) {
        return null; // Render nothing or a fallback UI if boardItems is not valid
    }

    return (
        <div className="board-frame" style={{ width: `${(gridSize / 5) * 4 + 24}%` }}>
            <div
                className="grid-container"
                ref={gridRef}
                style={{
                    gridTemplateColumns: `repeat(${gridSize}, 1fr)`,
                    gridTemplateRows: `repeat(${gridSize}, 1fr)`,
                    gap: `${(5 / gridSize) * 2}%`,
                }}
            >
                {Array.from({ length: gridSize * gridSize }, (_, index) => {
                    const row = Math.floor(index / gridSize);
                    const col = index % gridSize;

                    // Ensure the row and column exist before accessing them
                    const cardIcon = boardItems[row]?.[col] ?? null;

                    return (
                        <DropZone
                            key={index}
                            index={index}
                            size={size}
                            onDrop={onDrop}
                            cardIcon={cardIcon}
                            isDragging={isDragging}
                            hoveredIndex={hoveredIndex}
                            hoveredRotation={hoveredRotation}
                            setHoveredIndex={setHoveredIndex}
                            possiblePositions={possiblePositions}
                            setHoveredRotation={setHoveredRotation}
                        />
                    );
                })}
            </div>
        </div>
    );
}