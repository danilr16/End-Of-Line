import "../static/css/components/inGamePlayerList.css"
import request from "../util/request";
import InviteFriendsIcon from "./InviteFriendsIcon";

export default function InGamePlayerList({players,spectators,gamestate,username,gameCode,jwt,numPlayers, colors, playerTurnIndex}){

    const userIsPlayer = players.some((p)=>p.user.username === username);
    const userIsSpectator = spectators.some((p)=>p.username === username);
    const userSwitchRole = userIsPlayer ? "spectator" : "player"

    const handleSwitch = () =>{
        if (userIsPlayer && players.length > 1){
            request(`/api/v1/games/${gameCode}/leaveAsPlayer`, "PATCH", {}, jwt);
            setTimeout(()=>{request(`/api/v1/games/${gameCode}/joinAsSpectator`, "PATCH", {}, jwt);},10)
        }
        else if(userIsPlayer) alert("You can't become a spectator right now ")
        if(userIsSpectator && players.length < numPlayers){
            request(`/api/v1/games/${gameCode}/leaveAsSpectator`, "PATCH", {}, jwt);
            setTimeout(()=>{request(`/api/v1/games/${gameCode}/joinAsPlayer`, "PATCH", {}, jwt);},10)       
        }
        else if(userIsSpectator) alert("You can't become a player right now")
    }

    return(
        <div className="player-list-container">
            <ul className="player-list">
                <h5 style={{ color: "white" }}>
                    Players:
                </h5>

                {players.map((player, index) => (
                    <div 
                        className="player-container" 
                        key={index}
                        style={{
                            outline: index === playerTurnIndex 
                                ? `2px solid var(--player${colors[index]}-normal)` 
                                : "none",
                            outlineOffset: "4px", // Creates space between the container and the outline
                        }}
                    >
                        <div>
                            <p 
                                style={{
                                    color: `var(--player${colors[index]}-normal)`,
                                    textDecoration: player.playerState === "LOST" ? "line-through" : "none", // Add condition here
                                }} 
                                className="player-container-text"
                            >
                                {player.user.username}
                            </p>
                        </div>
                        {player.playerState === "LOST" && (
                            <div className="greyed-out-container">
                                
                            </div>
                        )}
                    </div>
                ))}
                {gamestate === "WAITING" && 
                <button className="player-list-button" onClick={handleSwitch}>{`Switch to ${userSwitchRole}`}</button>}
                {gamestate === "WAITING" && 
                <InviteFriendsIcon username = {username} gameCode = {gameCode}/>}
            </ul>
        </div>
    );

}