import React, { useState } from "react";

const DropZone = ({index,size,onDrop,cardIcon}) => {
    const [showDrop, setShowDrop] = useState(false);

    const canDropHere = () => {return true;}
    const handleDragEnter = () => {
        if(canDropHere()){ setShowDrop(true)}
    }
    const handleDragLeave = () => {setShowDrop(false)}
    const handleDrop = ()=> {
        setShowDrop(false)
        onDrop(index)
    }
    
    return(
        <div key={index} className={showDrop?"drop-zone-show":"drop-zone-hide"} style={{ minWidth: `${size}px`, minHeight: `${size}px`, position:"relative" }}
            onDragEnter={handleDragEnter} 
            onDragLeave={handleDragLeave}
            onDrop = {handleDrop}
            onDragOver={(e) => e.preventDefault()}>
            {cardIcon}
        </div>
    );

}

export default DropZone