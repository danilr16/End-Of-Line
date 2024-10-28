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
    const [gridSize, setGridSize] = useState(5); // TAMAÑO DEL TABLERO
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

    document.body.style.overflow = "hidden";

    const gridRef = useRef(null);
    const [gridItemSize, setGridItemSize] = useState(0);
    const [boardItems, setBoardItems] = useState(Array(gridSize).fill(null).map(() => Array(gridSize).fill(null))); // Estado para el tablero





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
    }, [gridSize,game]);


    useEffect(() => console.log(beingDraggedCard),[beingDraggedCard])
    useEffect(() => {
        if (!game) return;

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
    
    const handCards =[<GameCard  key = {0} size={gridItemSize} iconName = "t_rl_4_card" setBeingDraggedCard = {setBeingDraggedCard} index = {0} beingDraggedCard={beingDraggedCard} />,
        <GameCard key = {1}  size={gridItemSize} iconName = "t_fr_3_card" setBeingDraggedCard = {setBeingDraggedCard} index = {1} beingDraggedCard={beingDraggedCard}/>,
        <GameCard  key = {2} size={gridItemSize} iconName = "forward_1_card" setBeingDraggedCard = {setBeingDraggedCard} index = {2} beingDraggedCard={beingDraggedCard}/>,
        <GameCard  key = {3} size={gridItemSize} iconName = "l_r_2_card" setBeingDraggedCard = {setBeingDraggedCard} index = {3} beingDraggedCard={beingDraggedCard}/>]

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

    return (
        <div className="full-screen">
            <div className="half-screen">
                <div className="player-list-container">
                    <div className="player-list">
                        <h5 style={{ color: "white" }}>
                            Players:
                        </h5>
                    </div>
                </div>
                <Board gridSize={gridSize} gridItemSize={gridItemSize} gridRef={gridRef} onDrop = {onDrop} boardItems = {boardItems} />
                <div className="chat-container">
                    <div className="chat">
                        <p>
                            <span style={{ color: "grey" }}>Welcome to the chat! </span>
                        </p>
                        <p>
                            <span style={{ color: `var(--br-c-normal)` }}>{"[renfe_lover]: "} </span>
                            <span style={{ color: `white` }}>{"ivan putero"} </span>
                        </p>
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