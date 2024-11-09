import {  useEffect, useState, useRef } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { useParams } from "react-router-dom";
import { useColors } from "../ColorContext";
import Board from "../components/Board";
import GameCard from "../components/GameCard";
import { GameCardIcon } from '../components/GameCardIcon';
import "../static/css/screens/GameScreen.css"
import request from "../util/request";

export default function GameScreen() {
    const jwt = tokenService.getLocalAccessToken();
    const [gridSize, setGridSize] = useState(7); // TAMAÑO DEL TABLERO
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [isDragging, setDragging] = useState(false);
    const [hoveredIndex, setHoveredIndex] = useState(-1);
    const [beingDraggedCard, _setBeingDraggedCard] = useState(null);
    const gridRef = useRef(null);
    const beingDraggedCardRef = useRef(beingDraggedCard);
    const setBeingDraggedCard = (card) => {
        _setBeingDraggedCard(card);
        beingDraggedCardRef.current = card; 
    };
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

    const [gridItemSize, setGridItemSize] = useState(0);
    const [boardItems, setBoardItems] = useState(Array(gridSize).fill(null).map(() => Array(gridSize).fill(null)));
    
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
            isUsed={usedCards.has(index)}
        />
    ));

    const onDrop = (index) => {
        if (beingDraggedCardRef.current !== null) {
            const droppedCardIndex = beingDraggedCardRef.current; // This should refer to the index of the handCards
            const iconName = handCards[droppedCardIndex].props.iconName;
            const rowIndex = Math.floor(index / gridSize);
            const colIndex = index % gridSize;

            setBoardItems(prevBoardItems => {
                const newBoardItems = [...prevBoardItems];
                newBoardItems[rowIndex][colIndex] = <GameCardIcon iconName={iconName} />;
                return newBoardItems;
            });

            // Add the card to usedCards set
            setUsedCards(prev => new Set(prev).add(droppedCardIndex));

            setBeingDraggedCard(null); // Reset the dragged card
        }
    };

    useEffect(() => console.log(beingDraggedCard),[beingDraggedCard])
    useEffect(() => {
        
        if (!game) return;

        document.body.style.overflow = "hidden";
    });

    const [input, setInput] = useState('');
    const [chat, setChat] = useFetchState(
        [],
        `/api/v1/games/${gameCode}/chat`,
        jwt
    );
    const chatEndRef = useRef(null);
    const MAX_MESSAGES = 75; //Numero maximo de mensajes que se mostrarán en el chat

    useEffect(() => {
        if (user) {
            setUser(user)
        }
    }, [user])

    useEffect(() => {
        if(chatEndRef.current) {
            chatEndRef.current.scrollTop = chatEndRef.current.scrollHeight;
        }
    }, [chat]) //Esto es para que cuando se envíe un mensaje es scroll se vaya para abajo automáticamente


    const handleSendMessage = async () => {
        if (input.trim() === '') {
            return; //Para que no se envíen mensajes vacíos
        }
        console.log(JSON.stringify({userName: user.username, messageString:input}))

        const newChat = (await request(`/api/v1/games/${gameCode}/chat`,'PATCH',{userName:user.username, messageString:input},jwt)).resContent;

        try{
            setChat(newChat.slice(-MAX_MESSAGES))
        }
        catch(e){
            return;
        } 
        

        /*setChat((chat) => {
            const chatArray = [...chat, { text: input, userName: user.username }];
            return chatArray.slice(-MAX_MESSAGES); //Se queda con los ultimos mensajes que indique esa variable
        });*/
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
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, [gridSize, game]);

    if (!game) {
        return (
            <div className="half-screen">
                <p className="myGames-title">Cargando partida...</p>
            </div>
        );
    }


    

    return (
        <div className="full-screen">
            <div className="half-screen">
                <div className="player-list-container">
                    <ul className="player-list">
                        <h5 style={{ color: "white" }}>
                            Players:
                        </h5>

                        {players.map((player, index) => (
                            <div className="player-container" key={index}>
                                <div>
                                    <p className="player-container-text">{player.user.username}</p>
                                </div>
                            </div>
                        ))}

                    </ul>
                </div>
                <Board gridSize={gridSize} gridItemSize={gridItemSize} gridRef={gridRef} onDrop={onDrop} boardItems={boardItems} isDragging={isDragging} hoveredIndex={hoveredIndex} setHoveredIndex={setHoveredIndex} />
                <div className="chat-container">
                    <div className="chat">
                        

                        <div className="message-container" ref={chatEndRef}>
                            <span style={{ color: "grey"}}>Welcome to the chat! </span>
                            {chat.map((chatMessage, index) => (
                                <div key={index} className="chat-message">
                                    [{chatMessage.userName}]: <span className="message-content">{chatMessage.messageString}</span>
                                </div>
                            ))}
                        </div>
                        <input className="message-input"
                            type="text"
                            placeholder="Send your message..."
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={handlePressKey}
                            maxLength={500}
                        />
                    </div>
                </div>
            </div>
            <div className="bottom-container">
                <div className="card-container">
                    {handCards.filter((_, index) => !usedCards.has(index))}
                </div>
                <div className="card-deck" style={{ minWidth: `${gridItemSize}px`, minHeight: `${gridItemSize}px` }}>
                </div>
            </div>
        </div>
    );
}