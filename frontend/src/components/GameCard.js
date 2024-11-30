import React, { useEffect, useState, useRef } from 'react';
import classNames from "classnames";
import { CardOverlay } from './CardOverlay';
import "../static/css/components/gameCard.css"


const GameCard = ({ size, iconName, hoverable = true, beingDraggedCard, setBeingDraggedCard, index, setDragging, dropIndex, hoveredRotation, color, isUsed, canDrag}) => {
    const cardRef = useRef(null);
    const [position, setPosition] = useState({ top: window.innerHeight* 0.9691, left: window.innerWidth* 0.25083});
    const [isHovered, setIsHovered] = useState(false); 
    const isDragging = useRef(false); 

    

    const handleDragStart = () => {
        if (true) {
            isDragging.current = true; 
            setBeingDraggedCard(index);
            document.body.classList.add('no-select');
            setDragging(true);
        }
    };
    const handleDragEnd = () => {
        isDragging.current = false; 
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
                setPosition({
                    top: rect.top + ((isHovered && !isDragging.current) ? 0 : 10),
                    left: rect.left
                });
            }
        };
    
        const animationFrameId = requestAnimationFrame(function update() {
            updatePosition();
            requestAnimationFrame(update);
        });
    
        document.addEventListener('mouseup', handleDragEnd);
        document.addEventListener('mousemove', handleMouseMove);
    
    
        return () => {
            cancelAnimationFrame(animationFrameId);
            document.removeEventListener('mouseup', handleDragEnd);
            document.removeEventListener('mousemove', handleMouseMove);
        };
    }, [beingDraggedCard, index, isHovered]);

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


    if (isUsed) {
        return null; // or return <React.Fragment /> to return nothing
    }
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
            <CardOverlay iconName={iconName} size={size} position={position} isDragging={beingDraggedCard === index} dropIndex = {dropIndex} index={index} hoveredRotation = {hoveredRotation} color = {color}/>
        </React.Fragment>
    );
};

export default GameCard;