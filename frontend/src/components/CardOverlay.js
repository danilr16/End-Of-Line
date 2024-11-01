import React, { useState, useEffect, useRef } from "react";
import classNames from "classnames";
import { GameCardIcon } from "./GameCardIcon";

export const CardOverlay = ({ iconName, position, size, isDragging, dropIndex,index}) => {
    const [currentPosition, setCurrentPosition] = useState(position);
    const [currentRotation, setCurrentRotation] = useState(0);
    const animationFrameRef = useRef(null);
    const lastUpdateTimeRef = useRef(Date.now());

    const speed = 0.2;

    // Linear interpolation function
    const lerp = (start, end, factor) => start + (end - start) * factor;

    // Function to update position and rotation based on the target position
    const updatePosition = () => {
        setCurrentPosition((prevPosition) => ({
            top: lerp(prevPosition.top, position.top, speed),
            left: lerp(prevPosition.left, position.left, speed),
        }));
        setCurrentRotation((dropIndex === 13 && isDragging) ? 90 : 0);
        lastUpdateTimeRef.current = Date.now();
    };

    // Animation loop function
    const animate = () => {
        updatePosition();

        // Continue the animation loop
        animationFrameRef.current = requestAnimationFrame(animate);
    };

    useEffect(() => {
        // Start the animation loop when component mounts or `position` changes
        animationFrameRef.current = requestAnimationFrame(animate);

        return () => {
            // Clean up the animation frame on component unmount
            if (animationFrameRef.current) {
                cancelAnimationFrame(animationFrameRef.current);
            }
        };
    }, [position, isDragging, dropIndex]); // Re-run animation when position, isDragging, or dropIndex changes

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
            {iconName && <GameCardIcon iconName={iconName} size={size} />}
        </div>
    );
};