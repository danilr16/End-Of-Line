import React from 'react';
import classNames from "classnames";
import { GameCardIcon } from './GameCardIcon';


const GameCard = ({ size, iconName, hoverable = true , beingDraggedCard, setBeingDraggedCard,index}) => {

    
    const handleDragStart = () => {setBeingDraggedCard(index)}
    const handleDragEnd = () => {setBeingDraggedCard(null)}


    return (
        <div 
            className={classNames({'game-card-container':true, 'non-hoverable':!hoverable, 'dragging': beingDraggedCard&& beingDraggedCard === index})}
            draggable
            onDragStart={handleDragStart}
            onDragEnd={handleDragEnd}
            style={{ minWidth: `${size}px`, minHeight: `${size}px`, position: 'relative' }}

        >
            {iconName && <GameCardIcon iconName={iconName}/>}
        </div>
    );
};


export default GameCard;