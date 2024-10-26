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
    

  return (
    <div className="myGames-container">
        <h1 className="myGames-title">Mis Partidas</h1>
        {games.length === 0 ? (
            <p className="myGames-title">No hay partidas disponibles.</p>
        ) : (
            <ul className="myGames-table">
                {games.map((item, index) => (
            <div className="myGames-td" key={index}>
                <h4 className="myGames-tr">GameCode: {item.gameCode}</h4>
                <p className="myGames-tr">Host: {item.host.username}</p>
        </div>
      ))}
            </ul>
        )}
    </div>
);

   
}