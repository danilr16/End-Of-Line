import "../static/css/components/inGamePlayerList.css"
import request from "../util/request";

export default function InGamePlayerList({players,spectators,gamestate,username,gameCode,jwt,numPlayers}){

    const userIsPlayer = players.some((p)=>p.user.username === username);
    const userIsSpectator = spectators.some((p)=>p.user.username === username);
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
                    <div className="player-container" key={index}>
                        <div>
                            <p className="player-container-text">{player.user.username}</p>
                        </div>
                    </div>
                ))}
                {gamestate === "WAITING" && 
                <button className="player-list-button" onClick={handleSwitch}>{`Switch to ${userSwitchRole}`}</button>}
            </ul>
            
        </div>
    );

}