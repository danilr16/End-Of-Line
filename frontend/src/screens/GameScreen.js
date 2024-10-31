import jwt_decode from "jwt-decode";
import { useDebugValue, useEffect, useState, useRef } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { useParams } from "react-router-dom";
import { ColorProvider, useColors } from "../ColorContext";
import Board from "../components/Board";
import GameCard from "../components/GameCard";
import { GameCardIcon } from '../components/GameCardIcon';

export default function GameScreen() {
    const jwt = tokenService.getLocalAccessToken();
    const [gridSize, setGridSize] = useState(13); // TAMAÑO DEL TABLERO
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [beingDraggedCard, setBeingDraggedCard] = useState(null);
    const { colors, updateColors } = useColors();
    const { gameCode } = useParams();
    const [game, setGame] = useFetchState(
        null,
        `/api/v1/games/${gameCode}`,
        jwt,
        setMessage,
        setVisible
    );
    const [user, setUser] = useFetchState(
        null,
        '/api/v1/users/currentUser',
        jwt
    );
    const [players, setPlayers] = useState([]);

    document.body.style.overflow = "hidden";

    const gridRef = useRef(null);
    const [gridItemSize, setGridItemSize] = useState(0);
    const [boardItems, setBoardItems] = useState(Array(gridSize).fill(null).map(() => Array(gridSize).fill(null))); // Estado para el tablero
    const [input, setInput] = useState('');
    const [chatMessages, setChatMessages] = useState([]);
    const chatMessagesEndRef = useRef(null);
    const MAX_MESSAGES = 75; //Numero maximo de mensajes que se mostrarán en el chat



    useEffect(() => {
        // Function to update grid item size

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

        /*const animate = () => {
            updateGridItemSize();
            requestAnimationFrame(animate);
        };
        requestAnimationFrame(animate);*/
        window.addEventListener('resize', handleResize);

        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, [gridSize, game]);

    useEffect(() => {
        if (user) {
            setUser(user)
        }
    }, [user])

    useEffect(() => {
        if(chatMessagesEndRef.current) {
            chatMessagesEndRef.current.scrollTop = chatMessagesEndRef.current.scrollHeight;
        }
    }, [chatMessages]) //Esto es para que cuando se envíe un mensaje es scroll se vaya para abajo automáticamente


    const handleSendMessage = () => {

        if (input.trim() === '') {
            return; //Para que no se envíen mensajes vacíos
        }

        setChatMessages((chatMessages) => {
            const chatMessagesArray = [...chatMessages, { text: input, sender: user.username }];
            return chatMessagesArray.slice(-MAX_MESSAGES); //Se queda con los ultimos mensajes que indique esa variable
        });
        setInput('');
    }

    const handlePressKey = (e) => {
        if (e.key === 'Enter') {
            handleSendMessage();
        }
    }

    useEffect(() => console.log(beingDraggedCard), [beingDraggedCard])
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

    const handCards = [<GameCard key={0} size={gridItemSize} iconName="t_rl_4_card" setBeingDraggedCard={setBeingDraggedCard} index={0} beingDraggedCard={beingDraggedCard} />,
    <GameCard key={1} size={gridItemSize} iconName="t_fr_3_card" setBeingDraggedCard={setBeingDraggedCard} index={1} beingDraggedCard={beingDraggedCard} />,
    <GameCard key={2} size={gridItemSize} iconName="forward_1_card" setBeingDraggedCard={setBeingDraggedCard} index={2} beingDraggedCard={beingDraggedCard} />,
    <GameCard key={3} size={gridItemSize} iconName="l_r_2_card" setBeingDraggedCard={setBeingDraggedCard} index={3} beingDraggedCard={beingDraggedCard} />,
    <GameCard key={4} size={gridItemSize} iconName="l_l_2_card" setBeingDraggedCard={setBeingDraggedCard} index={4} beingDraggedCard={beingDraggedCard} />,
    ]

    const onDrop = (index) => {
        if (beingDraggedCard !== null) {
            const iconName = handCards[beingDraggedCard].props.iconName;
            const rowIndex = Math.floor(index / gridSize);
            const colIndex = index % gridSize;

            setBoardItems(prevBoardItems => {
                const newBoardItems = [...prevBoardItems];
                newBoardItems[rowIndex][colIndex] = <GameCardIcon iconName={iconName} />; // Almacena el icono en el tablero
                return newBoardItems;
            });
            setBeingDraggedCard(null); // Resetea el icono arrastrado
        }
    };

    if (!game) {
        return (
            <div className="half-screen">
                <p className="myGames-title">Cargando partida...</p>
            </div>
        );
    }
    console.log(game)
    return (
        <div className="full-screen">
            <div className="half-screen">
                <div className="player-list-container">
                    <div className="player-list">
                        <h5 style={{ color: "white" }}>
                            Players:
                        </h5>

                        <ul className="myGames-td">
                        {players.map((player, index) => (
                        <div   key={index}>
                            <p className="myGames-tr">User: {player.user.username}</p>
                            <p className="myGames-tr">Score: {player.score}</p>
                        </div>
                        ))}
                        </ul>

                    </div>
                </div>
                <Board gridSize={gridSize} gridItemSize={gridItemSize} gridRef={gridRef} onDrop={onDrop} boardItems={boardItems} />
                <div className="chat-container">
                    <div className="chat">
                        

                        <div className="message-container" ref={chatMessagesEndRef}>
                            <p>
                                <span style={{ color: "grey"}}>Welcome to the chat! </span>
                            </p>
                            {chatMessages.map((chatMessage, index) => (
                                <div key={index} className="chat-message">
                                    [{chatMessage.sender}]: <span className="message-content">{chatMessage.text}</span>
                                </div>
                            ))}
                        </div>
                        <input className="message-input"
                            type="text"
                            placeholder="Send your message..."
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyPress={handlePressKey} //aunque aparezca tachado hace falta
                        />
                    </div>
                </div>
            </div>
            <div className="bottom-container">
                <div className="card-container">
                    {handCards}
                </div>
                <div className="card-deck" style={{ minWidth: `${gridItemSize}px`, minHeight: `${gridItemSize}px` }}>
                </div>
            </div>
        </div>
    );
}