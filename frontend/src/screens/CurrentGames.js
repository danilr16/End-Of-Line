import React from 'react';
import "../static/css/components/components.css"
import GameContainer from '../components/GameContainer';
import tokenService from "../services/token.service";
import { useState } from 'react';
import { useEffect } from 'react';
import useFetchState from "../util/useFetchState";
import CreateModal from '../components/CreateModal';
import JoinGameModal from '../components/JoinGameModal';
import { ColorProvider,useColors } from "../ColorContext";


export default function CurrentGames(){
    const jwt = tokenService.getLocalAccessToken();

    const [isCreationModalOpen,setIsCreationModalOpen] = useState(false);
    const [isJoinModalOpen, setIsJoinModalOpen] = useState(false);
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false);
    const { colors, updateColors } = useColors();
    const [games,setGames] = useFetchState(
        [],
        `/api/v1/games/current`,
        jwt,
        setMessage,
        setVisible
    );
    console.log(games)

    const openCreationModal =  () =>setIsCreationModalOpen(true);
    const openJoinModal =  () =>setIsJoinModalOpen(true);
    const closeCreationModal =  () =>setIsCreationModalOpen(false);
    const closeJoinModal =  () =>setIsJoinModalOpen(false);
    const [selectedGamemode, setSelectedGamemode] = useState('');
    const [maxPlayers, setMaxPlayers] = useState(1); 
    const [isPrivateRoom, setIsPrivateRoom] = useState(false); 
    const [gameCode, setGameCode] = useState("");


    const parseGamemode = (gameMode) =>{
        switch(gameMode){
            case "VERSUS":
                return "Versus"
            case "TEAM_BATTLE":
                return "Team Battle"
            case "PUZZLE_COOP":
            case "PUZZLE_SINGLE":
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

    useEffect(() => {
        updateColors({
            light: 'var(--br-yellow-light)',
            normal: 'var(--br-yellow)',
            dark: 'var(--br-yellow-dark)',
        });
    }, []);

    
    console.log(games)
    const gamesToShow = games.map((game)=>parseGame(game))

    return (
        <>
            <div className={`curgames-options-div ${isCreationModalOpen || isJoinModalOpen ? "blurred" : ""}`}>
                <div className="curgames-button-container">
                    <button className="curgames-button" onClick={openCreationModal}>Create Game</button>
                </div>
                <div className="curgames-button-container">
                    <button className="curgames-button" onClick={openJoinModal}>Join with Code</button>
                </div>
                <div className="curgames-button-container">
                    <button className="curgames-button">Quick Join</button>
                </div>
            </div>
            <ul className={`current-games-container ${isCreationModalOpen || isJoinModalOpen ? "blurred" : ""}`}>
                {gamesToShow}
            </ul>

            {isCreationModalOpen && (
                <CreateModal                    
                    selectedGamemode={selectedGamemode}
                    setSelectedGamemode={setSelectedGamemode}
                    maxPlayers={maxPlayers}
                    setMaxPlayers={setMaxPlayers}
                    isPrivateRoom={isPrivateRoom}
                    setIsPrivateRoom={setIsPrivateRoom}
                    closeModal={closeCreationModal}
                />
            )}

            {isJoinModalOpen && (
                <JoinGameModal
                    gameCode={gameCode}
                    setGameCode={setGameCode}
                    closeModal={closeJoinModal}
                />
            )}
        </>
    );
}