import React, { useEffect, useState, useRef } from 'react';
import classNames from "classnames";
import { CardOverlay } from './CardOverlay';

const GameCard = ({ size, iconName, hoverable = true, beingDraggedCard, setBeingDraggedCard, index, setDragging }) => {
    const cardRef = useRef(null);
    const [position, setPosition] = useState({ top: 0, left: 0 });

    const handleDragStart = () => {
        setBeingDraggedCard(index);
        document.body.classList.add('no-select');
        setDragging(true);
    };

    const handleDragEnd = () => {
        setTimeout(() => {
            setBeingDraggedCard(null);
            document.body.classList.remove('no-select');
            setDragging(false);
        }, 0);
    };

    const handleMouseMove = (e) => {
        if (beingDraggedCard === index) {
            setPosition({ top: e.clientY - size/2, left: e.clientX - size/2});
        }
    };

    useEffect(() => {
        setTimeout(() => {
            if (cardRef.current) {
                const rect = cardRef.current.getBoundingClientRect();
                setPosition({ top: rect.top + 10, left: rect.left });
            }
        }, 30);

        document.addEventListener('mouseup', handleDragEnd);
        document.addEventListener('mousemove', handleMouseMove);

        return () => {
            document.removeEventListener('mouseup', handleDragEnd);
            document.removeEventListener('mousemove', handleMouseMove);
        };
    }, [beingDraggedCard, index]);

    return (
        <React.Fragment>
            <div 
                onMouseDown={handleDragStart}
                ref={cardRef}
                className={classNames({
                    'game-card-container': true, 
                    'non-hoverable': !hoverable, 
                    'dragging': beingDraggedCard === index
                })}
                style={{ minWidth: `${size}px`, minHeight: `${size}px` }}
            />
            <CardOverlay iconName={iconName} size={size} position={position} isDragging={beingDraggedCard === index}/>
        </React.Fragment>
    );
};

export default GameCard;