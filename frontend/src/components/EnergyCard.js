import React, { useState, useEffect, useRef } from 'react';
import { GameCardIcon } from './GameCardIcon';

import { ReactComponent as Accelerate } from '../static/images/energy-icons/accelerate.svg';
import { ReactComponent as BackAway } from '../static/images/energy-icons/back_away.svg';
import { ReactComponent as Brake } from '../static/images/energy-icons/brake.svg';
import { ReactComponent as ExtraGas } from '../static/images/energy-icons/extra_gas.svg';
import { ReactComponent as Lightning } from '../static/images/energy-icons/lightning.svg';
import { ReactComponent as JumpTeam } from '../static/images/energy-icons/jump_team.svg';

const EnergyCard = ({ 
    isRotating, 
    circleRef, 
    handleButtonClick, 
    gridItemSize, 
    playerRef,
    findColorById,
    gameMode
}) => {
    const [isCircleVisible, setCircleVisible] = useState(false);
    const buttonRef = useRef(null);

    const toggleCircleVisibility = () => {
        if (playerRef.current.energy === 0) {
            setCircleVisible(false);
        } else{
        setCircleVisible(prevState => !prevState);
        }
    };

    const handleSegmentClick = (action) => {
        handleButtonClick(action);
        setCircleVisible(false); 
    };

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (
                buttonRef.current && !buttonRef.current.contains(event.target) && 
                circleRef.current && !circleRef.current.contains(event.target)
            ) {
                setCircleVisible(false);
            }
        };

        document.addEventListener('click', handleClickOutside);

        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, [circleRef]);

    return (
        <div 
            className="energy-card" 
            style={{ width: gridItemSize, height: gridItemSize }}
        >
            <div className="icon-container">
            <GameCardIcon
                    iconName={'energy_card'}
                    color={findColorById(playerRef.current.id)}
                    rotation={3 - playerRef.current.energy}
                    smoothRotation={true} 
                />
                <button
                    className={`overlay-button ${isCircleVisible ? 'visible' : ''}`}
                    onClick={toggleCircleVisibility}
                    ref={buttonRef}
                >
                    <span className="button-text">{playerRef.current.energy}</span>
                    <Lightning
                        style={{
                            width: '30%',
                            height: '30%',
                            position: 'relative',
                            marginLeft:'4px',
                            filter: `drop-shadow(0px 0px 6px rgba(0, 0, 0, 1))`
                        }}
                    />
                </button>
            </div>
            {gameMode === "TEAM_BATTLE" && <button className={`extra-power ${isCircleVisible ? 'visible' : ''}`} onClick={() => handleSegmentClick("JUMP_TEAM")}>
                <JumpTeam
                    style={{
                        width: '80%',
                        height: '80%',
                        rotate: '2deg',
                        position: 'relative',   
                        top: '0%',
                        left: '2%',
                    }}
                />
            </button>}
            <div 
                className={`circle ${isRotating ? 'rotate' : ''} ${isCircleVisible ? 'visible' : ''}`} 
                ref={circleRef}
            >               
                <div className="inner-circle"></div>

                
                <button 
                    className={`segment top-left ${!isCircleVisible ? 'disabled' : ''}`} 
                    onClick={() => handleSegmentClick("ACCELERATE")}
                    disabled={!isCircleVisible}
                >
                    <Accelerate
                        style={{
                            width: '20%',
                            height: '20%',
                            rotate: '-45deg',
                            position: 'relative',   
                            top: '-6%',
                            left: '-6%',
                        }}
                    />
                </button>
                <button 
                    className={`segment top-right ${!isCircleVisible ? 'disabled' : ''}`} 
                    onClick={() => handleSegmentClick("BRAKE")}
                    disabled={!isCircleVisible}
                >
                    <Brake
                        style={{
                            width: '20%',
                            height: '20%',
                            rotate: '-45deg',
                            position: 'relative',   
                            top: '-6%',
                            left: '6%',
                        }}
                    />
                </button>
                <button 
                    className={`segment bottom-left ${!isCircleVisible ? 'disabled' : ''}`} 
                    onClick={() => handleSegmentClick("BACK_AWAY")}
                    disabled={!isCircleVisible}
                >
                    <BackAway
                        style={{
                            width: '20%',
                            height: '20%',
                            rotate: '-45deg',
                            position: 'relative',   
                            top: '5%',
                            left: '-7%',
                        }}
                    />
                </button>
                <button 
                    className={`segment bottom-right ${!isCircleVisible ? 'disabled' : ''}`} 
                    onClick={() => handleSegmentClick("EXTRA_GAS")}
                    disabled={!isCircleVisible}
                >
                    <ExtraGas
                        style={{
                            width: '20%',
                            height: '20%',
                            rotate: '-45deg',
                            position: 'relative',   
                            top: '6%',
                            left: '6%',
                        }}
                    />
                </button>
            </div>
        </div>
    );
};

export default EnergyCard;