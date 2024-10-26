import React from 'react';
import "../static/css/components/components.css"
import GameContainer from '../components/GameContainer';
import tokenService from "../services/token.service";
import { useState } from 'react';
import useFetchState from "../util/useFetchState";


export default function CurrentGames(){
    const jwt = tokenService.getLocalAccessToken();

    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false);
    const [games,setGames] = useFetchState(
        [],
        `/api/v1/games/current`,
        jwt,
        setMessage,
        setVisible
    );
    console.log(games)

    const parseGamemode = (gameMode) =>{
        switch(gameMode){
            case "VERSUS":
                return "Versus"
            case "TEAM_BATTLE":
                return "Team Battle"
            case "PUZZLE_COOP":
                return "Puzzle"
            default:
                return "";
        }
    }
    const parseGamestate = (gameState) =>{
        switch(gameState){
            case "WAITING":
                return "Waiting for players"
            case "IN_PROCESS":
                return "In progress"
            default:
                return "";
        }
    }

    const parseGame = (game) =>{
        return(<GameContainer key = {game.gameCode} 
            gameCode = {game.gameCode} 
            gameMode = {parseGamemode(game.gameMode)}
            state = {parseGamestate(game.gameState)}
            host = {game.host.username}
            playerNumber = {game.players.length}
            maxPlayers = {game.numPlayers}
            spectatorsNumber = {game.spectators.length}
            isPrivate = {!game.isPublic}
            />)
    }

    console.log(games)
    const gamesToShow = games.map((game)=>parseGame(game))

    return(
        <ul className = "current-games-container"> 
           {gamesToShow}
        </ul>
    );

}