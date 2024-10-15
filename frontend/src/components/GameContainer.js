import React from 'react';
import "../static/css/components/components.css"

export default function GameContainer(props) {

    return(
    <li className="game-li-container" key = {props.gameCode}>
        <div className = "game-li-content">
            <div className = "game-header">
                <h4 className = "game-code"> {props.gameCode} </h4> 
                <h4 className="game-mode"> {props.gameMode} </h4>
            </div>
            <div className = "game-info">
                <span className = "state">  {props.state} </span>
                <span className = "host">  {props.host} </span>
            </div>
            
            <div className = "game-info">
                <div className = "stat-item">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"    
                    strokeWidth="1.5" stroke="currentColor" className="players-icon">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
                    </svg> 
                    <p className="player-number">8</p>
                </div>

                <div className = "stat-item">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"    
                    strokeWidth="1.5" stroke="currentColor" className="spectators-icon">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178Z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                    </svg> 
                    <p className="spectator-number">8</p>
                </div>
            </div>

        
        </div>

    </li>
);

}