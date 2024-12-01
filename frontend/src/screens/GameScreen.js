import {  useEffect, useState, useRef } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { useParams, useNavigate} from "react-router-dom";
import { useColors } from "../ColorContext";
import Board from "../components/Board";
import GameCard from "../components/GameCard";
import { GameCardIcon } from '../components/GameCardIcon';
import "../static/css/screens/GameScreen.css"
import request from "../util/request";
import ChatBox from "../components/ChatBox";
import InGamePlayerList from "../components/InGamePlayerList";
import LeaveConfirmationModal from "../components/LeaveConfirmationModal";
import randomShuffle from "../util/randomShuffle";

export default function GameScreen() {
    const jwt = tokenService.getLocalAccessToken();
    const navigate = useNavigate();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const { gameCode } = useParams();

    const [game, setGame] = useFetchState(
        null,
        `/api/v1/games/${gameCode}`,
        jwt,
        setMessage,
        setVisible
    );

    const [requestSentAt, setRequestSentAt] = useState(null); 
    const [adjustedTimerStart, setAdjustedTimerStart] = useState(null);

    useEffect(() => {
        const fetchGameUpdates = () => {
            // Record the local time when the request is made
            setRequestSentAt(Date.now());

            fetch(`/api/v1/games/${gameCode}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            })
                .then(response => response.json())
                .then(data => {
                    if (!data.message) {
                        setGame(data); // Update game state
                    } else {
                        if (setMessage !== null) {
                            setMessage(data.message);
                            setVisible(true);
                        } else {
                            window.alert(data.message);
                        }
                    }
                })
                .catch(error => {
                    console.error("Error fetching game updates:", error);
                    setMessage("Failed to fetch data");
                    setVisible(true);
                });
        };

        const interval = setInterval(fetchGameUpdates, 1000);

        return () => clearInterval(interval); // Cleanup on unmount
    }, [gameCode, jwt, setMessage, setVisible, setGame]);

    useEffect(() => {
        if (game?.turn) {
            const curPlayer = game.players[findPlayerIndexById(game.turn)]
            if (game?.timestamp && curPlayer?.turnStarted && requestSentAt) {
                // Parse backend and player timestamps
                const backendTimestamp = new Date(game.timestamp).getTime();
                const playerTurnStarted = new Date(curPlayer.turnStarted).getTime();
    
                // Calculate the local-adjusted timer start timestamp
                const networkDelay = Date.now() - requestSentAt;
                const adjustedTime = playerTurnStarted + networkDelay;
    
                setAdjustedTimerStart(new Date(adjustedTime).toISOString());
            }
        }
        
    }, [game?.timestamp, requestSentAt]);

    const [secondsLeft, setSecondsLeft] = useState(null);

    useEffect(() => {
        if (adjustedTimerStart) {
            const targetTime = new Date(adjustedTimerStart).getTime() + 5 * 60 * 1000; // Adjusted time + 5 minutes

            const updateSecondsLeft = () => {
                const currentTime = Date.now();
                const diff = Math.max(0, Math.floor((targetTime - currentTime) / 1000)); // Calculate seconds left
                setSecondsLeft(diff);
            };

            // Update the countdown every second
            const interval = setInterval(updateSecondsLeft, 1000);
            updateSecondsLeft(); // Initial call to set the state immediately

            return () => clearInterval(interval); // Cleanup on unmount
        }
    }, [adjustedTimerStart]);

    const cardNameMapping = {
        "INICIO": "start_card",
        "BLOCK_CARD": "block_card",
        "TYPE_0": "cross_0_card",
        "TYPE_5": "cross_5_card",
        "ENERGY_CARD": "energy_card",
        "TYPE_1": "forward_1_card",
        "TYPE_2_IZQ": "l_l_2_card",
        "TYPE_2_DER": "l_r_2_card",
        "TYPE_3_IZQ": "t_fl_3_card",
        "TYPE_3_DER": "t_fr_3_card",
        "TYPE_4": "t_rl_4_card",
    };

    const [showConfirmationModal, setShowConfirmationModal] = useState(false) //Modal de confirmación para salir de la partida
    const [isDragging, setDragging] = useState(false);
    const [hoveredIndex, setHoveredIndex] = useState(-1);
    const [hoveredRotation, setHoveredRotation] = useState(0);
    const [beingDraggedCard, _setBeingDraggedCard] = useState(null);
    const gridRef = useRef(null);
    const beingDraggedCardRef = useRef(beingDraggedCard);
    const setBeingDraggedCard = (card) => {
        _setBeingDraggedCard(card);
        beingDraggedCardRef.current = card; 
    };
    const { colors, updateColors } = useColors();


    const [player, setPlayer] = useState(null);

    const playerRef = useRef(player);
    useEffect(() => {
        playerRef.current = player;
    }, [player]);

    const [fetchedCards, setFetchedCards] = useState([]);
    useEffect(() => {
        if (player && player.hand && player.hand.cards) {
            const updatedCards = player.hand.cards.map((card) => ({
                id: card.id,
                type: cardNameMapping[card.type],
            }));
            setFetchedCards(updatedCards);
        }
    }, [player]);

    const fetchedCardsRef = useRef(fetchedCards);
    useEffect(() => {
        fetchedCardsRef.current = fetchedCards;
    }, [fetchedCards]);

    const [currentCards, setCurrentCards] = useState([]); 

    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentCards((prevCurrentCards) => {
                const nextCard = fetchedCardsRef.current.find(
                    (card) => !prevCurrentCards.some((currentCard) => currentCard.id === card.id)
                );

                if (nextCard) {
                    return [...prevCurrentCards, nextCard];
                }
                return prevCurrentCards;
            });
        }, 100); 

        return () => clearInterval(interval);
    }, []); 

    useEffect(() => {
        console.log("Updated current cards:", currentCards);
    }, [currentCards]); 

    const currentCardsRef = useRef(currentCards);
    useEffect(() => {
        currentCardsRef.current = currentCards;
    }, [currentCards]);

    useEffect(() => {
        if (user && game && game.players && game.players.length > 0) {
        // Find the player that matches the username from game data
        const currentPlayer = game.players.find(p => p.user.username === user.username);

        if (currentPlayer) {
            // Set the player state if found
            setPlayer(currentPlayer);
        } else {
            // Optionally, handle the case when the player is not found
            setPlayer(null);
        }
        } else {
        // If the game or players are null or empty, reset player state
        setPlayer(null);
        }
    }, [game]); // Run this effect whenever the 'game' state changes
    

    const [gridSize, setGridSize] = useState(1); // TAMAÑO DEL TABLERO
    useEffect(() => {
        if (game && game.tableCard) {
            setGridSize(game.tableCard.numRow);
        }
    }, [game]); // Dependency on 'game' to watch for updates

    

    const [user, setUser] = useFetchState(
        null,
        '/api/v1/users/currentUser',
        jwt
    );
    const [players, setPlayers] = useState([]);
    
    const [playerColors, setPlayerColors] = useState([]);

    const playerColorsRef = useRef(playerColors);
    useEffect(() => {
        playerColorsRef.current = playerColors;
    }, [playerColors]);
    
    function findPlayerIndexById(id) {
        return players.findIndex(player => player.id === id);
    }

    function findColorById(id) {
        return playerColorsRef.current[findPlayerIndexById(id)];
    }

    const colorMapping = {
        'PUZZLE_COOP':[2,1,3,4,5,6,7,8,9,10],
        'PUZZLE_SINGLE':[2,1,3,4,5,6,7,8,9,10],
        'VERSUS':[1,2,3,4,5,6,7,8,9,10],
        'TEAM_BATTLE':[3,2,1,4,5,6,7,8,9,10]
    } 

    useEffect(() => {
        if (game && game.gameMode) {
            setPlayerColors(randomShuffle(gameCode,players.length,colorMapping[game.gameMode]));
        }
    }, [players,game?.gameMode])

    const [gridItemSize, setGridItemSize] = useState(0);
    const [boardItems, setBoardItems] = useState(
        Array(gridSize).fill(null).map(() => Array(gridSize).fill(null))
    );
    
    useEffect(() => {
        if (game !== null && game.tableCard !== null) {
            const { rows } = game.tableCard;
            let prevBoardItems = [...boardItems];  // Copy the current boardItems
            
            // Check if the boardItems is the correct size, if not, initialize a new one
            if (boardItems.length !== gridSize || boardItems[0].length !== gridSize) {
                prevBoardItems = Array(gridSize).fill(null).map(() => Array(gridSize).fill(null));
            }

            // Iterate through rows and cells of game.tableCard
            rows.forEach((row, rowIndex) => {
                row.cells.forEach((cell, colIndex) => {
                    if (cell.isFull && cell.card) {
                        const cardName = cell.card.type;
                        const updatedCell = (
                            <GameCardIcon 
                                key={`${rowIndex}-${colIndex}`} 
                                iconName={cardNameMapping[cardName]} 
                                rotation={cell.card.rotation} 
                                color={findColorById(cell.card.playerId)} 
                            />
                        );

                        // Ensure the row exists, initialize it if necessary
                        if (!prevBoardItems[rowIndex]) {
                            prevBoardItems[rowIndex] = [];
                        }
                        
                        // Ensure the cell exists, initialize it if necessary
                        if (prevBoardItems[rowIndex][colIndex] === undefined) {
                            prevBoardItems[rowIndex][colIndex] = null;
                        }

                        // Now, update the cell at the given index
                        prevBoardItems[rowIndex][colIndex] = updatedCell;
                    }
                });
            });

            // Set the boardItems to the updated prevBoardItems
            setBoardItems(prevBoardItems);
        }
    }, [game]);

    // New state to track used cards
    const [usedCards, setUsedCards] = useState(new Set());

    const handCards = [
        { iconName: "t_rl_4_card" },
        { iconName: "t_fr_3_card" },
        { iconName: "forward_1_card" },
        { iconName: "l_r_2_card" },
        { iconName: "t_fl_3_card" },
    ].map((card, index) => (
        <GameCard
            key={index}
            size={gridItemSize}
            iconName={card.iconName}
            setBeingDraggedCard={setBeingDraggedCard}
            index={index}
            beingDraggedCard={beingDraggedCard}
            setDragging={setDragging}
            dropIndex={hoveredIndex}
            hoveredRotation={hoveredRotation}
            isUsed={usedCards.has(index)}
            color = {player ? findColorById(player.id) : 1}
        />
    ));

    

    const onDrop = (index, rot) => {
        if (beingDraggedCardRef.current !== null) {
            const droppedCardIndex = beingDraggedCardRef.current; // This should refer to the index of the handCards
            const card = currentCardsRef.current[droppedCardIndex];
            const cardId = card.id;
            const iconName = card.type;
            const rowIndex = Math.floor(index / gridSize);
            const colIndex = index % gridSize;

            setBoardItems(prevBoardItems => {
                const newBoardItems = [...prevBoardItems];
                newBoardItems[rowIndex][colIndex] = <GameCardIcon iconName={iconName} color={findColorById(playerRef.current.id)} rotation = {rot} />;
                return newBoardItems;
            });
    
            // Add the card to usedCards set
            setUsedCards(prev => new Set(prev).add(cardId));
    
            setBeingDraggedCard(null); // Reset the dragged card

            console.log(cardId)
            console.log(index-1)

            const url = new URL(`/api/v1/games/${gameCode}/placeCard`, window.location.origin);
            url.searchParams.append("cardId", cardId);
            url.searchParams.append("index", index+1);

            // Use fetch to send the PATCH request with query parameters
            fetch(url, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwt}`, // Pass JWT token as Authorization header
                },
            })
            .then((response) => response.json()) // Parse the response as JSON
            .then((data) => {
                // Handle the response if needed
                console.log("Card placed successfully:", data);
                if (data.statusCode == 403) {
                    setBoardItems(prevBoardItems => {
                        const newBoardItems = [...prevBoardItems];
                        newBoardItems[rowIndex][colIndex] = null;
                        return newBoardItems;
                    });
                    setUsedCards((prev) => {
                        const updatedSet = new Set(prev);
                        if (updatedSet.has(cardId)) {
                            updatedSet.delete(cardId);
                        }
                        return updatedSet;
                    });
                } 
            })
            .catch((error) => {
                // Handle any errors that occur during the request
                console.error("Error placing card:", error);
            });
            }
    };
    
    useEffect(() => {
        console.log(usedCards)
    }, [usedCards]);

    useEffect(() => {
        
        if (!game) return;

        document.body.style.overflow = "hidden";
    });

    useEffect(() => {
        if(!jwt){
            navigate("/login")
        }
        if (user) {
            setUser(user)
        }
    }, [user])

    useEffect(() => {
        if (!game) return;
        if (game.players) {
            setPlayers(game.players);
        }

        const gameMode = game.gameMode;

        switch (gameMode) {
            case 'PUZZLE_COOP':
            case 'PUZZLE_SINGLE':
                updateColors({
                    light: 'var(--br-green-light)',
                    normal: 'var(--br-green)',
                    dark: 'var(--br-green-dark)',
                });
                break;
            case 'VERSUS':
                updateColors({
                    light: 'var(--br-blue-light)',
                    normal: 'var(--br-blue)',
                    dark: 'var(--br-blue-dark)',
                });
                break;
            case 'TEAM_BATTLE':
                updateColors({
                    light: 'var(--br-red-light)',
                    normal: 'var(--br-red)',
                    dark: 'var(--br-red-dark)',
                });
                break;
            default:
                updateColors({
                    light: 'var(--br-yellow-light)',
                    normal: 'var(--br-yellow)',
                    dark: 'var(--br-yellow-dark)',
                });
                break;
        }
    }, [game, updateColors]);

    useEffect(() => {
        const updateGridItemSize = () => {
            if (gridRef.current) {
                const itemSize = gridRef.current.clientWidth / gridSize;
                setGridItemSize(itemSize * 0.9);
            }
        };

        updateGridItemSize();
        const handleResize = () => {
            updateGridItemSize();
        };

        window.addEventListener('resize', handleResize);

        const intervalId = setInterval(updateGridItemSize, 1000);
        return () => {
            window.removeEventListener('resize', handleResize);
            clearInterval(intervalId);
        };
    }, [gridSize, game, gridRef.current]);

    

    useEffect(() => { //Join on entering screen
        if (game && players && game.numPlayers && user && user.username) {
            const isPlayerInGame = players.some(player => player.user.username === user.username);
            const isSpectatorInGame = game.spectators.some(sp => sp.username === user.username);

            if (!isPlayerInGame && !isSpectatorInGame && players.length < game.numPlayers) { //join as player if possible
                request(`/api/v1/games/${gameCode}/joinAsPlayer`, "PATCH", {}, jwt);
            }
            else if(!isSpectatorInGame && !isPlayerInGame){ //join as Spectator if possible
                request(`/api/v1/games/${gameCode}/joinAsSpectator`, "PATCH", {}, jwt);
            }
        }
    }, [players]);

    if ((!game || !user) && jwt) {
        return (
            <p className="waiting-sign">Loading game...</p>
        );
    }

    const modalVisibility = () => {
        
        if(game.gameState !== 'END') { //Si la partida ha finalizado, no pide confirmación para salir
            setShowConfirmationModal(true);
        }
    }
    

    const handleLeave = () => {
        if (game && players && game.numPlayers && user && user.username) {
            const isPlayerInGame = players.some(player => player.user.username === user.username)
            const isSpectatorInGame = game.spectators.some(spectator => spectator.username === user.username)
            if(isPlayerInGame) {
                request(`/api/v1/games/${gameCode}/leaveAsPlayer`, "PATCH", {}, jwt)
            } else if (isSpectatorInGame) {
                request(`/api/v1/games/${gameCode}/leaveAsSpectator`, "PATCH", {}, jwt)
            }
            navigate('/games/current')
        }
    };

    const handleStart = () => {
        if (game && players && game.numPlayers && user && user.username && game.host.username == user.username) {
            request(`/api/v1/games/${gameCode}/startGame`, "PATCH", {}, jwt)
        }
    };

    const useEnergy = () => {
        console.log(playerRef.current.energy);
    };
    
    return (
        <div className="full-screen">
            <div className="half-screen">
                <InGamePlayerList players = {players} spectators = {game.spectators} colors = {playerColors}
                    gamestate={game.gameState} username = {user.username} gameCode = {gameCode} jwt={jwt} numPlayers={game.numPlayers} playerTurnIndex = {game.gameState == "IN_PROCESS" ? findPlayerIndexById(game.turn): null}/>

                    {(game.tableCard !== null && gridSize > 0 && Array.isArray(boardItems) && boardItems.length > 0) && (
                        <Board 
                            gridSize={gridSize} 
                            gridItemSize={gridItemSize} 
                            gridRef={gridRef} 
                            onDrop={onDrop} 
                            boardItems={boardItems} 
                            isDragging={isDragging} 
                            hoveredIndex={hoveredIndex} 
                            hoveredRotation={hoveredRotation}
                            setHoveredIndex={setHoveredIndex}
                            setHoveredRotation={setHoveredRotation}
                            possiblePositions = {player?.possiblePositions?.map((position, index) => ({
                                position: position,
                                rotation: player?.possibleRotations?.[index], 
                            })) || []} 
                            canDrop = {player && game.turn === player.id && game.gameState == "IN_PROCESS"}
                            secondsLeft = {secondsLeft}
                            state = {game.gameState}
                        />
                    )}

                {(game.tableCard == null && game.host.username == user.username) && (<button className="start-game-button" onClick={handleStart}>START GAME</button>)}

                {(game.tableCard == null && game.host.username !== user.username) && (<div className="waiting-sign"> 
                    Waiting for host...
                </div>)}

                <ChatBox gameCode={gameCode} user={user} jwt={jwt} colors = {playerColors}/>
            </div>
            <div className="bottom-container">
                <div className="card-container">
                    {currentCards.map((card, index) => (
                        <GameCard
                            key={index}
                            size={gridItemSize}
                            iconName={card.type}
                            setBeingDraggedCard={setBeingDraggedCard}
                            index={index}
                            beingDraggedCard={beingDraggedCard}
                            setDragging={setDragging}
                            dropIndex={hoveredIndex}
                            hoveredRotation={hoveredRotation}
                            isUsed={usedCards.has(card.id)}
                            color = {player ? findColorById(player.id) : 1}
                            id = {card.id}
                            canDrag = {game.turn === player.id && game.gameState == "IN_PROCESS"}
                        />
                    )).filter((card, index) => !usedCards.has(card.id))}
                </div>
                {player && <div
                    className="card-deck"
                    style={{
                        minWidth: `${gridItemSize}px`,
                        minHeight: `${gridItemSize}px`,
                        backgroundColor: `var(--player${findColorById(player.id)}-normal)`, // Dynamically set normal color
                        color: `var(--player${findColorById(player.id)}-dark)`, // Dynamically set dark color
                        boxShadow: `
                            0px -1px 0px var(--player${findColorById(player.id)}-normal),
                            0px -2px 0px var(--player${findColorById(player.id)}-dark),
                            0px -3px 0px var(--player${findColorById(player.id)}-normal),
                            0px -4px 0px var(--player${findColorById(player.id)}-dark),
                            0px -5px 0px var(--player${findColorById(player.id)}-normal),
                            0px -6px 0px var(--player${findColorById(player.id)}-dark),
                            0px -7px 0px var(--player${findColorById(player.id)}-normal),
                            0px -8px 0px var(--player${findColorById(player.id)}-dark),
                            0px -9px 0px var(--player${findColorById(player.id)}-normal),
                            0px -10px 0px var(--player${findColorById(player.id)}-dark),
                            0px -11px 0px var(--player${findColorById(player.id)}-normal),
                            0px -12px 0px var(--player${findColorById(player.id)}-dark)
                        `,
                    }}
                >
                </div>}
            </div>
            {playerRef && playerRef.current != null && <div className="energy-card" style={{"width": gridItemSize, "height": gridItemSize}}>
                <div className="icon-container">
                    <GameCardIcon
                        iconName={'energy_card'}
                        color={findColorById(playerRef.current.id)}
                        rotation={3 - playerRef.current.energy}
                    />
                    <button
                        className="overlay-button"
                        onClick={useEnergy}
                    />
                </div>
            </div>
            }
            {game.gameState !== 'END' && <button className="leave-button" onClick={modalVisibility}>
                    Leave game
            </button>}
            <LeaveConfirmationModal showConfirmationModal={showConfirmationModal} setShowConfirmationModal = {setShowConfirmationModal} handleLeave={handleLeave} game={game} user={user} />
        </div>
    );
}