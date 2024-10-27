import jwt_decode from "jwt-decode";
import { useDebugValue, useEffect, useState } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { useParams } from "react-router-dom";

export default function GameInProcess(){
    const jwt = tokenService.getLocalAccessToken();

    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false);

    const { gameCode } = useParams();
    const [game,setGame] = useFetchState(
        null,
        `/api/v1/games/${gameCode}`,
        jwt,
        setMessage,
        setVisible
    );

    if(!game) { 
        return <p className="myGames-title">Cargando partida...</p>
    }

    console.log(game)

    return (
        <div className="myGames-title">
            <h1 className="myGames-title">Pantalla en proceso</h1>
        </div>
    )

}