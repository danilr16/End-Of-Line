import React, { useState } from "react";

const DropZone = ({index,size,onDrop,cardIcon}) => {
    const [showDrop, setShowDrop] = useState(false);
    const [isHovered, setIsHovered] = useState(true);

    const handleMouseEnter = () => {
      setIsHovered(false)
      console.log(isHovered)
    };
  
    const handleMouseLeave = () => {
      setIsHovered(true)
      console.log(isHovered)

    };

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
            onDragOver={(e) => e.preventDefault()}
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}>
            {cardIcon}
        </div>
    );

}

export default DropZone