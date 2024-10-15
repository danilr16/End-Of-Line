import React from 'react';
import "../static/css/components/components.css"
import GameContainer from './GameContainer';

export default function CurrentGames(){

    return(
        <ul className = "current-games-container"> 
            <GameContainer gameCode = {25555} gameMode = "Team Battle" state = "Waiting players" host = "lfb9498"/>
            <GameContainer gameCode = {30315} gameMode = "Versus" state = "In progress" host = "lfb9498"/>
            <GameContainer gameCode = {35531} gameMode = "Versus" state = "waiting" host = "lfb9498"/>
            <GameContainer gameCode = {40165} gameMode = "Versus" state = "waiting" host = "lfb9498"/>
            <GameContainer gameCode = {49495} gameMode = "Versus" state = "waiting" host = "lfb9498"/>
            
        </ul>
    );

}