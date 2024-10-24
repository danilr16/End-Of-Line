import React from 'react';
import "../static/css/components/components.css"
import GameContainer from './GameContainer';

export default function CurrentGames(){

    return(
        <ul className = "current-games-container"> 
            <GameContainer key = {25555} gameCode = {25555} gameMode = "Team Battle" state = "Waiting players" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {true} />
            <GameContainer key= {30315} gameCode = {30315} gameMode = "Versus" state = "In progress" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {false}/>
            <GameContainer key = {35531} gameCode = {35531} gameMode = "Versus" state = "waiting" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {false}/>
            <GameContainer key = {40165} gameCode = {40165} gameMode = "Versus" state = "waiting" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {false}/>
            <GameContainer key = {49495} gameCode = {49495} gameMode = "Versus" state = "waiting" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {false}/>
            
        </ul>
    );

}