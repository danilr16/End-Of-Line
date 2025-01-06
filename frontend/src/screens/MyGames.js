import useFetchState from "../util/useFetchState";
import { useDebugValue, useEffect, useState } from "react";
import tokenService from "../services/token.service";
import jwt_decode from "jwt-decode";
import "../static/css/screens/MyGames.css"
import MyGameModal from '../components/MyGameModal';
import { ReactComponent as Skull } from '../static/images/skull.svg';
import { ReactComponent as Trophy } from '../static/images/trophy.svg';


export default function MyGames(){

    const jwt = tokenService.getLocalAccessToken();
    const [selectedGame, setSelectedGame] = useState(null); //game selected to show data
    const[isGameDataModalOpen, setIsGameDataModalOpen] = useState(false);
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false);
    const [games,setGames] = useFetchState(
        [],
        `/api/v1/users/games`,
        jwt,
        setMessage,
        setVisible
    );

    if(!games) { 
        return <p className="myGames-title">Cargando partidas...</p>
    }

    const openGameDataModal = (game) => {
        setSelectedGame(game);
        setIsGameDataModalOpen(true);
    };
    const closeGameDataModal = () => {
        setSelectedGame(null);
        setIsGameDataModalOpen(false);
    };
    

    const gamePlayer = (game) => { //jugador del usuario logueado
        //el usuario logueado se accede mediante el token y la propiedad sub
        return game.players.find(player => player.user.username == jwt_decode(jwt).sub);
    }

    const win_lost = (game) => {
        const player = game.players.find(player => player.user.username == jwt_decode(jwt).sub);
        console.log(game);
        if(player.playerState == "WON") return `myGames-td myGames-WON`;
        return "myGames-td myGames-LOST";
    }
    
    const parseGamemode = (gameMode) =>{
        switch(gameMode){
            case "VERSUS":
                return "Versus"
            case "TEAM_BATTLE":
                return "Team Battle"
            case "PUZZLE_COOP":
                return "Puzzle Cooperative"
            case "PUZZLE_SINGLE":
                return "Single Player"
            default:
                return "";
        }
    }
  return (
    <div className="myGames-container">
        <h1 className="myGames-title">My Games</h1>
        {games.length === 0 ? (
            <p className="myGames-title">No games available.</p>
        ) : (
            <ul className="myGames-table">
                {games.map((item, index) => (
            <div className= {win_lost(item)} key={index} onClick={() => openGameDataModal(item)} style={{ cursor: 'pointer' }}>
                <span className="myGames-tr">GameMode: {parseGamemode(item.gameMode)}</span>
                {gamePlayer(item).playerState === "LOST" && (
                                                <Skull
                                                    style={{
                                                        color: 'darkred',
                                                        width: '25px', 
                                                        height: '25px',
                                                        marginLeft: '10px', 
                                                        position:'relative',
                                                        
                                                    }}
                                                />
                                            )}
                {gamePlayer(item).playerState === "WON" && (
                                                <Trophy
                                                    style={{
                                                        color: 'darkgreen',
                                                        width: '25px', 
                                                        height: '25px',
                                                        marginLeft: '10px', 
                                                        position:'relative',
                                                    }}
                                                />
                                            )}

        </div>
      ))}
            </ul>
        )}

    {isGameDataModalOpen && (
                <MyGameModal selectedGame={selectedGame} closeGameDataModal={closeGameDataModal} parseGamemode={parseGamemode(selectedGame.gameMode)} winLost={gamePlayer(selectedGame).playerState} />
            )}
    </div>
);

   
}