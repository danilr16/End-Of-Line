import React from 'react';
import classNames from "classnames";
import { useEffect,useState,useRef } from 'react';
import { CardOverlay } from './CardOverlay';

//Carta invisible, setea la posicion del elemento visual
const GameCard = ({ size, iconName, hoverable = true , beingDraggedCard, setBeingDraggedCard,index}) => {
    const cardRef = useRef(null);
    const [position, setPosition] = useState({top:0,left:0})



    
    const handleDragStart = () => {setBeingDraggedCard(index)}
    const handleDragEnd = () => {setBeingDraggedCard(null)}

    useEffect(() => {
        setTimeout(()=>{
            if (cardRef.current) {
            const rect = cardRef.current.getBoundingClientRect();
            setPosition({top:rect.top+10,left:rect.left});
            console.log(rect.top,rect.left)
        }},30)

    }, []);


    return (
        <React.Fragment>
        <div 
            ref={cardRef}
            className={classNames({'game-card-container':true, 'non-hoverable':!hoverable, 'dragging': beingDraggedCard&& beingDraggedCard === index})}
            draggable
            onDragStart={handleDragStart}
            onDragEnd={handleDragEnd}
            style={{ minWidth: `${size}px`, minHeight: `${size}px`}}
        />
        <CardOverlay iconName={iconName} size = {size} position= {position}/>
        </React.Fragment>
        
    );
};


export default GameCard;