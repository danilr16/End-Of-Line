import React, { useState, useEffect, useRef } from "react";
import "../static/css/components/dropZone.css"


const DropZone = ({ index, size, onDrop, cardIcon, isDragging, hoveredIndex, setHoveredIndex, possiblePositions, hoveredRotation, setHoveredRotation }) => {
    const [showDrop, setShowDrop] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    
    const [rotation, setRotation] = useState(null);

    

    const isHoveredRef = useRef(isHovered);
    const hoveredIndexRef = useRef(hoveredIndex);
    const hoveredRotationRef = useRef(hoveredRotation);
    const isDraggingRef = useRef(isDragging);

    useEffect(() => {
        isHoveredRef.current = isHovered;
    }, [isHovered]);

    useEffect(() => {
        hoveredIndexRef.current = hoveredIndex;
    }, [hoveredIndex]);
    
    useEffect(() => {
        hoveredRotationRef.current = hoveredRotation;
    }, [hoveredRotation]);

    const setRotationFromPositions = () => {
        const positionData = possiblePositions.find(item => item.position === index + 1);
        if (positionData) {
            setRotation(positionData.rotation);
        }
        else {
            setRotation(0)
        }
    };

    useEffect(() => {
        setRotationFromPositions();
    }, [possiblePositions, index]);


    useEffect(() => {
        isDraggingRef.current = isDragging;
    }, [isDragging]);

    const handleMouseEnter = () => {
        if (canDropHere()) {
            setIsHovered(true);
            setHoveredIndex(index)
            setHoveredRotation(rotation);
        }
    };

    const handleMouseLeave = () => {
        setIsHovered(false);
        if (hoveredIndex === index) {
            setHoveredIndex(-1);
            setHoveredRotation(0);
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

    const canDropHere = () => {
        return isDropPossible(index, possiblePositions);
    };

    const handleDrop = () => {
        handleMouseLeave();
        onDrop(index); 
    };

    const isDropPossible = (index, possiblePositions) => {
        return Array.isArray(possiblePositions) &&
            possiblePositions.some(item => item.position === index + 1);
    };

    return (
        <div 
            key={index} 
            className={`drop-zone 
                ${hoveredIndex === index && isDraggingRef.current && isHovered ? "drop-zone-show" : "drop-zone-hide"} 
                ${(isDropPossible(index, possiblePositions) && isDragging) ? "drop-zone-possible" : ""} 
                ${(!isDropPossible(index, possiblePositions) && isDragging) ? "drop-zone-darkened" : ""}
                ${(!(cardIcon === null)) ? "drop-zone-trans-border" : ""}`}
            style={{
                minWidth: `${size}px`,
                minHeight: `${size}px`,
                position: "relative",
            }}
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
        >
            {cardIcon}
        </div>
    );
};

export default DropZone;