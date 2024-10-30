import React from "react";
import { GameCardIcon } from "./GameCardIcon";

//Elemento visible que sigue el movimiento del drag(pronto)
export const CardOverlay  = ({iconName,position,size}) =>{
    return(
        <div 
            className="card-overlay"
            style={{
            position: 'fixed',        // Set position absolute
            top: position.top,           // Use top and left from GameCard position
            left: position.left,
            minWidth: `${size}px`,
            minHeight: `${size}px`,
        }}>
            {iconName && <GameCardIcon iconName={iconName} size = {size}/>}
        </div>
    );
}