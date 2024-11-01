import React, { useEffect, useState, useRef } from 'react';
import classNames from "classnames";
import { CardOverlay } from './CardOverlay';
import "../static/css/components/gameCard.css"


const GameCard = ({ size, iconName, hoverable = true, beingDraggedCard, setBeingDraggedCard, index, setDragging, dropIndex }) => {
    const cardRef = useRef(null);
    const [position, setPosition] = useState({ top: window.innerHeight* 0.9691, left: window.innerWidth* 0.25083});
    const [isHovered, setIsHovered] = useState(false); // State to track hover status
    const isDragging = useRef(false); // Ref to track dragging status
    const handleDragStart = () => {
        isDragging.current = true; // Set dragging ref to true
        setBeingDraggedCard(index);
        document.body.classList.add('no-select');
        setDragging(true);
    };

    const handleDragEnd = () => {
        isDragging.current = false; // Set dragging ref to false
        setTimeout(() => {
            setBeingDraggedCard(null);
            document.body.classList.remove('no-select');
            setDragging(false);
        }, 0);
    };

    const handleMouseMove = (e) => {
        if (beingDraggedCard === index) {
            setPosition({ top: e.clientY - size / 2, left: e.clientX - size / 2 });
        }
    };

    useEffect(() => {
        const updatePosition = () => {
            if (cardRef.current && !isDragging.current) {
                const rect = cardRef.current.getBoundingClientRect();
                setPosition({ top: rect.top + ((isHovered && !isDragging.current) ? 0 : 10), left: rect.left });
            }
        };

        // Set timeout to update position on initial load
        const timeoutId = setTimeout(updatePosition, 30);

        // Add event listeners for mouse events
        document.addEventListener('mouseup', handleDragEnd);
        document.addEventListener('mousemove', handleMouseMove);

        // Add resize event listener
        window.addEventListener('resize', updatePosition);

        return () => {
            // Cleanup listeners
            clearTimeout(timeoutId);
            document.removeEventListener('mouseup', handleDragEnd);
            document.removeEventListener('mousemove', handleMouseMove);
            window.removeEventListener('resize', updatePosition);
        };
    }, [beingDraggedCard, index, isHovered]);

    // Hover event handlers
    const handleMouseEnter = () => {
        if (hoverable) {
            setIsHovered(true);
        }
    };

    const handleMouseLeave = () => {
        if (hoverable) {
            setIsHovered(false);
        }
    };

    return (
        <React.Fragment>
            <div
                onMouseDown={handleDragStart}
                onMouseEnter={handleMouseEnter}
                onMouseLeave={handleMouseLeave}
                ref={cardRef}
                className={classNames({
                    'game-card-container': true,
                    'non-hoverable': !hoverable,
                    'dragging': beingDraggedCard === index || isDragging.current, // Use ref to check dragging state
                    'hovered': isHovered // Optional: add a class when hovered
                })}
                style={{ minWidth: `${size}px`, minHeight: `${size+10}px` }}
            />
            <CardOverlay iconName={iconName} size={size} position={position} isDragging={beingDraggedCard === index} dropIndex = {dropIndex} index={index} />
        </React.Fragment>
    );
};

export default GameCard;