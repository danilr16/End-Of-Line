import useFetchState from "../util/useFetchState";
import { useDebugValue, useEffect, useState } from "react";
import tokenService from "../services/token.service";
import getErrorModal from "../util/getErrorModal";
import jwt_decode from "jwt-decode";



export default function MyGames(){

    const jwt = tokenService.getLocalAccessToken();

    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false);
    const [games,setGames] = useFetchState(
        null,
        `/api/v1/users/games`,
        jwt,
        setMessage,
        setVisible
    );

    if(!games) { 
        return <p className="myGames-title">Cargando partidas...</p>
    }

    
    const playerData = games.map((item, index) => 
        item.players.find(player => player.username === item.host.username)
    );

  return (
    <div className="myGames-container">
        <h1 className="myGames-title">My Games</h1>
        {games.length === 0 ? (
            <p className="myGames-title">No games available.</p>
        ) : (
            <ul className="myGames-table">
                {games.map((item, index) => (
            <div className="myGames-td" key={index}>
                <span className="myGames-tr">GameMode: {item.gameMode}</span>
                <span className="myGames-tr">Score: { playerData[index]? playerData[index].score:"N/A" }</span>
        </div>
      ))}
            </ul>
        )}
    </div>
);

   
}