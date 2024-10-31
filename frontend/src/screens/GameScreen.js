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
    const [gridSize, setGridSize] = useState(7);
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

    const [gridItemSize, setGridItemSize] = useState(0);
    const [boardItems, setBoardItems] = useState(Array(gridSize).fill(null).map(() => Array(gridSize).fill(null)));
    
    // New state to track used cards
    const [usedCards, setUsedCards] = useState(new Set());

    const handCards = [
        { key: 0, iconName: "t_rl_4_card" },
        { key: 1, iconName: "t_fr_3_card" },
        { key: 2, iconName: "forward_1_card" },
        { key: 3, iconName: "l_r_2_card" }
    ].map(card => (
        <GameCard
            key={card.key}
            size={gridItemSize}
            iconName={card.iconName}
            setBeingDraggedCard={setBeingDraggedCard}
            index={card.key}
            beingDraggedCard={beingDraggedCard}
            setDragging={setDragging}
            // Pass usedCards to the GameCard to determine if it should be displayed
            isUsed={usedCards.has(card.key)}
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
                    <div className="player-list">
                        <h5 style={{ color: "white" }}>
                            Players:
                        </h5>
                        <ul className="myGames-td">
                            {game.players.map((item, index) => (
                                <div key={index}>
                                    <p className="myGames-tr">User: {item.user.username}</p>
                                    <p className="myGames-tr">Score: {item.score}</p>
                                </div>
                            ))}
                        </ul>
                    </div>
                </div>
                <Board gridSize={gridSize} gridItemSize={gridItemSize} gridRef={gridRef} onDrop={onDrop} boardItems={boardItems} isDragging={isDragging} hoveredIndex={hoveredIndex} setHoveredIndex={setHoveredIndex} />
                <div className="chat-container">
                    <div className="chat">
                        <p>
                            <span style={{ color: "grey" }}>Welcome to the chat! </span>
                        </p>
                        <p>
                            <span style={{ color: `var(--br-c-normal)` }}>{"[renfe_lover]: "} </span>
                            <span style={{ color: `white` }}>{"Hola!"} </span>
                        </p>
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