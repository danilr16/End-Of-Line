import React from 'react';
import DropZone from './DropZone';

export default function Board({ gridSize, size, gridRef,onDrop,boardItems,isDragging,hoveredIndex,setHoveredIndex }) {
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
                {Array.from({ length: Math.floor((gridSize * gridSize)) }, (_, index) => (
                    <DropZone key = {index} index = {index} size = {size} onDrop = {onDrop} cardIcon = {boardItems[Math.floor(index / gridSize)][index % gridSize]} isDragging={isDragging} hoveredIndex = {hoveredIndex} setHoveredIndex = {setHoveredIndex}/>
                        
                ))}
            </div>
        </div>
    );
}