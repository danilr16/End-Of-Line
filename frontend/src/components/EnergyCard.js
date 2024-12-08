import React from 'react';
import { GameCardIcon } from '../components/GameCardIcon';

const EnergyCard = ({ gridItemSize, playerRef, useEnergy, color }) => {
    if (!playerRef || !playerRef.current) return null;

    return (
        <div
            className="energy-card"
            style={{ width: gridItemSize, height: gridItemSize }}
        >
            <div className="icon-container">
                <GameCardIcon
                    iconName="energy_card"
                    color={color}
                    rotation={3 - playerRef.current.energy}
                />
                <button
                    className="overlay-button"
                    onClick={useEnergy}
                />
            </div>
        </div>
    );
};

export default EnergyCard;