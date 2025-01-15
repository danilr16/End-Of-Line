import "../static/css/components/inGamePlayerList.css"
import request from "../util/request";
import InviteFriendsIcon from "./InviteFriendsIcon";
import { useAlert } from "../AlertContext";

import { ReactComponent as Skull } from '../static/images/skull.svg';
import { ReactComponent as Eye } from '../static/images/eye.svg';
import { ReactComponent as Trophy } from '../static/images/trophy.svg';
import { ReactComponent as Lightning } from '../static/images/energy-icons/lightning.svg';

export default function InGamePlayerList({players,spectators,gamestate,username,gameCode,jwt,numPlayers, colors, playerTurnIndex, maxPlayers}){

    const {updateAlert} = useAlert();
    const userIsPlayer = players.some((p)=>p.user.username === username);
    const userIsSpectator = spectators.some((p)=>p.username === username);
    const userSwitchRole = userIsPlayer ? "spectator" : "player"

    const handleSwitch = async () =>{
        if (userIsPlayer && players.length > 1){
            const res = await request(`/api/v1/games/${gameCode}/switchToSpectator`, "PATCH", {}, jwt);
            if(res.error) updateAlert("Error. You are not friends with every player")
        }
        else if(userIsPlayer) updateAlert("You can't become a spectator right now ");
        if(userIsSpectator && players.length < numPlayers){
            request(`/api/v1/games/${gameCode}/switchToPlayer`, "PATCH", {}, jwt);     
        }
        else if(userIsSpectator) updateAlert("You can't become a player right now");
    }

    return(
        <>
        <div className="player-list-container">
            <ul className="player-list">
                <h5 style={{ color: "white" }}>
                    Players: {gamestate === 'WAITING' ? `(${players.length}/${maxPlayers})` : ''}
                </h5>

                {players.map((player, index) => (
                    <div 
                        className="player-container" 
                        key={index}
                        style={{
                            outline: index === playerTurnIndex 
                                ? `2px solid var(--player${colors[index]}-normal)` 
                                : "none",
                            outlineOffset: "4px",
                        }}
                    >
                        <div 
                            style={{
                                display: 'flex',
                                alignItems: 'center', 
                                justifyContent: 'flex-start', 
                            }}
                        >
                            <img src={player.user.image} className="small-profile-image" />
                            <p 
                                style={{
                                    color: `var(--player${colors[index]}-normal)`,
                                    textDecoration: player.playerState === "LOST" ? "line-through" : "none",
                                    WebkitTextStroke: colors[index] > 10 ? `0.4px var(--player${colors[index]}-dark)` : "0.4px transparent",
                                }}
                                className="player-container-text"
                            >
                                {player.user.username}
                            </p>
                            {player.playerState === "LOST" && (
                                <Skull
                                    style={{
                                        width: '16px', 
                                        height: '16px',
                                        marginLeft: '10px', 
                                        position:'relative',
                                        opacity:'0.5',
                                        color: 'white',
                                    }}
                                />
                            )}
                            {player.playerState === "WON" && (
                                <Trophy
                                    style={{
                                        width: '16px', 
                                        height: '16px',
                                        marginLeft: '10px', 
                                        position:'relative',
                                        color:'rgb(255, 195, 66)',
                                    }}
                                />
                            )}
                            {player.energyUsedThisRound === true && (
                                <Lightning
                                    style={{
                                        width: '16px', 
                                        height: '16px',
                                        marginLeft: '10px', 
                                        position:'relative',
                                        opacity:'0.8'
                                    }}
                                />
                            )}
                            
                            <p 
                                style={{
                                    color: 'var(--br-trans-grey-very-light)',
                                    position: 'absolute', 
                                    right: 20, 
                                    top: '50%', 
                                    transform: 'translateY(-50%)', 
                                    fontSize: '80%'
                                }}
                                className="player-container-text"
                            >
                                {player.score == 0 ? '' : player.score}
                            </p>
                        </div>
                        {player.playerState === "LOST" && (
                            <div className="greyed-out-container">
                                
                            </div>
                        )}
                    </div>
                ))}

                {spectators.map((spectator, index) => (
                    <div 
                        className="player-container" 
                        key={index+players.length}
                    >
                        <div 
                            style={{
                                display: 'flex',
                                alignItems: 'center', 
                                justifyContent: 'flex-start', 
                            }}
                        >
                            <img src={spectator.image} className="small-profile-image" />
                            <p 
                                style={{
                                    color: `var(--br-trans-grey-very-light)`,
                                }} 
                                className="player-container-text"
                            >
                                {spectator.username}
                            </p>
                            {true && (
                                <Eye
                                    style={{
                                        width: '16px', 
                                        height: '16px',
                                        marginLeft: '10px', 
                                        position:'relative',
                                        opacity:'0.5'
                                    }}
                                />
                            )}
                        </div>
                            <div className="greyed-out-container">
                                
                            </div>
                    </div>
                ))}
                
            </ul>
            
        </div>
        {gamestate === "WAITING" && 
                <button className="player-list-button" onClick={handleSwitch}>{`Switch to ${userSwitchRole}`}</button>}
                {gamestate === "WAITING" && 
                <InviteFriendsIcon username = {username} gameCode = {gameCode}/>}
        </>
    );

}