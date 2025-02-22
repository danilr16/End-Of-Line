import React from 'react';
import DropZone from './DropZone';
import "../static/css/components/board.css"

export default function Board({ gridSize, size, gridRef, onDrop, boardItems, isDragging, hoveredIndex, setHoveredIndex, possiblePositions, hoveredRotation, state, setHoveredRotation, canDrop, secondsLeft, turn, round}) {
    

    
    return (
        <>
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
                                canDrop={canDrop}
                            />
                        );
                    })}
                </div>
                {turn && canDrop && <p className="turn-sign" >IT'S YOUR TURN!</p>}
            </div>
            {state === "IN_PROCESS" && <p className="seconds-left">{secondsLeft}</p>}
            {state === "IN_PROCESS" && <p className="current-turn">TURN {round}</p>}
            
        </> 
    );
}