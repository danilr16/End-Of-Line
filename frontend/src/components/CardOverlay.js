import React, { useState, useEffect, useRef } from "react";
import classNames from "classnames";
import { GameCardIcon } from "./GameCardIcon";
import "../static/css/components/cardOverlay.css"


export const CardOverlay = ({ iconName, position, size, isDragging, dropIndex,index, hoveredRotation, color}) => {
    const [currentPosition, setCurrentPosition] = useState(position);
    const [currentRotation, setCurrentRotation] = useState(0);
    const animationFrameRef = useRef(null);
    const lastUpdateTimeRef = useRef(Date.now());
    const hoveredRotationRef = useRef(hoveredRotation);

    const speed = 0.3;

    const lerp = (start, end, factor) => start + (end - start) * factor;

    const updatePosition = () => {
        setCurrentPosition((prevPosition) => ({
            top: lerp(prevPosition.top, position.top, speed),
            left: lerp(prevPosition.left, position.left, speed),
        }));
        setCurrentRotation(!isDragging ? 0: ((hoveredRotation+1)%4-1)*90);
        lastUpdateTimeRef.current = Date.now();
    };

    const animate = () => {
        updatePosition();

        animationFrameRef.current = requestAnimationFrame(animate);
    };

    useEffect(() => {
        animationFrameRef.current = requestAnimationFrame(animate);

        return () => {
            if (animationFrameRef.current) {
                cancelAnimationFrame(animationFrameRef.current);
            }
        };
    }, [position, isDragging, dropIndex]); 

    return (
        <div
            className={classNames({
                "card-overlay": true,
                "dragging": isDragging
            })}
            style={{
                position: "fixed",
                top: currentPosition.top,
                left: currentPosition.left,
                minWidth: `${size}px`,
                minHeight: `${size}px`,
                zIndex: isDragging ? 2 : 1,
                transform: `rotate(${currentRotation}deg)`
            }}
        >
            {iconName && <GameCardIcon iconName={iconName} size={size} color = {color} />}
        </div>
    );
};