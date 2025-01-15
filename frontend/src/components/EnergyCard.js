import React, { useState, useEffect, useRef } from 'react';
import { GameCardIcon } from './GameCardIcon';

import { ReactComponent as Accelerate } from '../static/images/energy-icons/accelerate.svg';
import { ReactComponent as BackAway } from '../static/images/energy-icons/back_away.svg';
import { ReactComponent as Brake } from '../static/images/energy-icons/brake.svg';
import { ReactComponent as ExtraGas } from '../static/images/energy-icons/extra_gas.svg';
import { ReactComponent as Lightning } from '../static/images/energy-icons/lightning.svg';
import { ReactComponent as Lock } from '../static/images/lock.svg';
import { ReactComponent as JumpTeam } from '../static/images/energy-icons/jump_team.svg';

const EnergyCard = ({ 
    isRotating, 
    circleRef, 
    handleButtonClick, 
    gridItemSize, 
    playerRef,
    findColorById,
    gameMode,
    turn
}) => {
    const [isCircleVisible, setCircleVisible] = useState(false);
    const [tooltip, setTooltip] = useState({ visible: false, x: 0, y: 0, title: '', content: '' });
    const buttonRef = useRef(null);

    const toggleCircleVisibility = () => {
        if (turn >= 3) {
            if (playerRef.current.energy === 0) {
                setCircleVisible(false);
            } else {
                setCircleVisible(prevState => !prevState);
            }
        }
    };

    const handleSegmentClick = (action) => {
        handleButtonClick(action);
        setCircleVisible(false); 
    };

    const handleMouseEnter = (event, title, content) => {
        setTooltip({
            visible: true,
            bottom: window.innerHeight - event.pageY + 10, 
            right: window.innerWidth - event.pageX + 10, 
            title,
            content,
        });
    };
    
    const handleMouseMove = (event, title, content) => {
        if (tooltip.visible) {
            setTooltip({
                ...tooltip,
                bottom: window.innerHeight - event.pageY + 10,  
                right: window.innerWidth - event.pageX + 10,    
                title,                                         
                content,                                        
            });
        }
    };
    const handleMouseLeave = () => {
        setTooltip({ visible: false});
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
        <><div 
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
                <div
                    className={`overlay-button ${turn < 3 ? 'visible' : ''} lock`}
                    onClick={toggleCircleVisibility}
                    
                >
                    <Lock
                        style={{
                            width: '30%',
                            height: '30%',
                            top: '-2%',
                            position: 'relative',
                            filter: `drop-shadow(0px 0px 6px rgba(0, 0, 0, 1))`
                        }}
                    />
                </div>
                {turn >= 3 && <button
                    className={`overlay-button ${isCircleVisible ? 'visible' : ''} energy`}
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
                </button>}
            </div>
            {gameMode === "TEAM_BATTLE" && <button className={`extra-power ${isCircleVisible ? 'visible' : ''}`} onClick={() => handleSegmentClick("JUMP_TEAM")}
                onMouseEnter={(e) => handleMouseEnter(e, 'JUMP LINE', 'Skip your teamatte\'s line.')}
                onMouseMove={(e) => handleMouseMove(e, 'JUMP LINE', 'Skip your teamatte\'s line.')}
                onMouseLeave={handleMouseLeave}>
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
                    onMouseEnter={(e) => handleMouseEnter(e, 'ACCELERATE', 'Place 3 cards your turn instead of 2.')}
                    onMouseMove={(e) => handleMouseMove(e, 'ACCELERATE', 'Place 3 cards your turn instead of 2.')}
                    onMouseLeave={handleMouseLeave}
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
                    onMouseEnter={(e) => handleMouseEnter(e, 'BRAKE', 'Place 1 card your turn instead of 2.')}
                    onMouseMove={(e) => handleMouseMove(e, 'BRAKE', 'Place 1 card your turn instead of 2.')}
                    onMouseLeave={handleMouseLeave}
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
                    onMouseEnter={(e) => handleMouseEnter(e, 'BACK AWAY', 'Place your next card following the second-to-last card you played.')}
                    onMouseMove={(e) => handleMouseMove(e, 'BACK AWAY', 'Place your next card following the second-to-last card you played.')}
                    onMouseLeave={handleMouseLeave}
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
                    onMouseEnter={(e) => handleMouseEnter(e, 'EXTRA GAS', 'Gain 1 additional card in your hand.')}
                    onMouseMove={(e) => handleMouseMove(e, 'EXTRA GAS', 'Gain 1 additional card in your hand.')}
                    onMouseLeave={handleMouseLeave}
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
            <div 
            className="energy-tooltip" 
            style={{
                position: 'fixed',
                right: tooltip.right,
                bottom: tooltip.bottom,
                backgroundColor: 'var(--br-grey)',
                color: '#fff',
                padding: '10px',
                borderRadius: '5px',
                maxWidth: 200,
                pointerEvents: 'none',
                zIndex: 1000,
                boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
                opacity: tooltip.visible ? 1 : 0, 
                transition: 'opacity 0.3s ease', 
            }}
        >
            <div className="tooltip-title" style={{ fontSize: '0.75rem', fontWeight: 'bold', marginBottom: '5px', textAlign: 'center' }}>
                {tooltip.title}
            </div>
            <div className="tooltip-content" style={{ fontSize: '0.6rem', textAlign: 'left', lineHeight: '1.2' }}>
                {tooltip.content}
            </div>
        </div></>
    );
};

export default EnergyCard;