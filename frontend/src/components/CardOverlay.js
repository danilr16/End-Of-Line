import React, { useState, useEffect, useRef } from "react";
import classNames from "classnames";
import { GameCardIcon } from "./GameCardIcon";

export const CardOverlay = ({ iconName, position, size , isDragging}) => {
    const [currentPosition, setCurrentPosition] = useState(position);
    const intervalRef = useRef(null);
    const remainingTimeRef = useRef(16); // Duration of each interval in ms (16 milliseconds)
    const lastUpdateTimeRef = useRef(Date.now());

    // Linear interpolation function
    const lerp = (start, end, factor) => start + (end - start) * factor;

    // Function to update the position
    const updatePosition = () => {
        setCurrentPosition((prevPosition) => ({
            top: lerp(prevPosition.top, position.top, 0.4),
            left: lerp(prevPosition.left, position.left, 0.4),
        }));
        lastUpdateTimeRef.current = Date.now(); // Reset last update time
    };

    // Function to start or restart the interval
    const startInterval = (remainingTime) => {
        if (intervalRef.current) {
            clearInterval(intervalRef.current);
        }

        intervalRef.current = setInterval(() => {
            const now = Date.now();
            const timeSinceLastUpdate = now - lastUpdateTimeRef.current;

            if (timeSinceLastUpdate >= remainingTime) {
                updatePosition(); // Update the position
            } else {
                // Update remaining time
                remainingTimeRef.current = remainingTime - timeSinceLastUpdate;
            }
        }, 16); // Check every 16 ms
    };

    useEffect(() => {
        // Calculate the time left when the position changes
        const timeLeft = remainingTimeRef.current - (Date.now() - lastUpdateTimeRef.current);

        // If time left is 1 ms or less, immediately update the position
        if (timeLeft <= 1) {
            updatePosition();
            remainingTimeRef.current = 16; // Reset remaining time to 16 ms
        }

        startInterval(timeLeft < 0 ? 16 : timeLeft); // Start with 16 ms if negative

        return () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        };
    }, [position]); // Run on position change

    return (
        <div
            className={classNames({
                "card-overlay" : true,
                'dragging': isDragging
            })}
            style={{
                position: 'fixed',
                top: currentPosition.top,
                left: currentPosition.left,
                minWidth: `${size}px`,
                minHeight: `${size}px`,
            }}
        >
            {iconName && <GameCardIcon iconName={iconName} size={size} />}
        </div>
    );
};