import React, { useState, useEffect, useRef } from "react";

const DropZone = ({ index, size, onDrop, cardIcon, isDragging, hoveredIndex, setHoveredIndex }) => {
    const [showDrop, setShowDrop] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    
    const isHoveredRef = useRef(isHovered);
    const hoveredIndexRef = useRef(hoveredIndex);
    const isDraggingRef = useRef(isDragging);

    useEffect(() => {
        isHoveredRef.current = isHovered;
    }, [isHovered]);

    useEffect(() => {
        hoveredIndexRef.current = hoveredIndex;
    }, [hoveredIndex]);

    // Sync isDraggingRef with isDragging prop
    useEffect(() => {
        isDraggingRef.current = isDragging;
    }, [isDragging]);

    const handleMouseEnter = () => {
        setIsHovered(true);
        setHoveredIndex(index);
    };

    const handleMouseLeave = () => {
        setIsHovered(false);
        if (hoveredIndex === index) {
            setHoveredIndex(-1);
        }
    };

    const handleMouseUp = () => {
        if (isDraggingRef.current && isHoveredRef.current) {
            handleDrop();
        }
    };
    
    useEffect(() => {
        window.addEventListener('mouseup', handleMouseUp);
        
        return () => {
          window.removeEventListener('mouseup', handleMouseUp);
        };
    }, []);

    const canDropHere = () => true;

    const handleDrop = () => {
        handleMouseLeave();
        onDrop(index); 
    };
    
    return (
        <div 
            key={index} 
            className={(hoveredIndex === index && isDraggingRef.current) ? "drop-zone-show" : "drop-zone-hide"}
            style={{ minWidth: `${size}px`, minHeight: `${size}px`, position: "relative" }}
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
        >
            {cardIcon}
        </div>
    );
};

export default DropZone;