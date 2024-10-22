import React from 'react';
import "../static/css/components/testpalet.css"
import GameContainer from './GameContainer';
import { Button, ButtonGroup } from "reactstrap";

export default function PaletColorTest(){

    return(
        <div>
        <ul className = "current-games-container"> 
            <GameContainer key = {25555} gameCode = {25555} gameMode = "Team Battle" state = "Waiting players" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {true} />
            <GameContainer key= {30315} gameCode = {30315} gameMode = "Versus" state = "In progress" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {false}/>
            <GameContainer id="a" key= {30315} gameCode = {30315} gameMode = "Versus" state = "In progress" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {false}/>
            <GameContainer id="b" key= {30315} gameCode = {30315} gameMode = "Versus" state = "In progress" host = "lfb9498" playerNumber = {8} maxPlayers = {8} spectatorsNumber = {8} isPrivate = {false}/>

        </ul>
        <ButtonGroup className="test-button-group">
            <Button size="lg" className="test-button">Test 1</Button>
            <Button size="lg" className="test-button2">Test 2</Button>
            <Button size="lg" className="test-button3">Test 3</Button>
            <Button size="lg" className="test-button4">Test 4</Button>
        </ButtonGroup>
        </div>
    );

}