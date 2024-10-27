import jwt_decode from "jwt-decode";
import { useDebugValue, useEffect, useState, useRef } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { useParams } from "react-router-dom";
import { ColorProvider, useColors } from "../ColorContext";
import Board from "../components/Board";

export default function GameScreen() {
    const jwt = tokenService.getLocalAccessToken();

    const [gridSize, setGridSize] = useState(5); // TAMAÑO DEL TABLERO

    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const { colors, updateColors } = useColors();
    const { gameCode } = useParams();

    const [game, setGame] = useFetchState(
        null,
        `/api/v1/games/${gameCode}`,
        jwt,
        setMessage,
        setVisible
    );

    const gridRef = useRef(null);
    const [gridItemSize, setGridItemSize] = useState(0);

    useEffect(() => {
        if (!gridRef.current) return;

        const updateGridItemSize = () => {
            if (gridRef.current) {
                const itemSize = gridRef.current.clientWidth / gridSize;
                setGridItemSize(itemSize);
            }
        };

        updateGridItemSize();
        window.addEventListener('resize', updateGridItemSize);

        return () => {
            window.removeEventListener('resize', updateGridItemSize);
        };
    }, [gridSize]);

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

    if (!game) {
        return (
            <div className="full-screen">
                <p className="myGames-title">Cargando partida...</p>
            </div>
        );
    }

    return (
        <div className="full-screen">
            <div className="player-list-container">
                <div className="player-list">
                    <h5 style={{color:"white"}}>
                        Players:
                    </h5>
                </div>
            </div>
            <Board gridSize={gridSize} gridItemSize={gridItemSize} gridRef={gridRef} />
            <div className="chat-container">
                <div className="chat">
                    <p>
                        <span style={{color:"grey"}}>Welcome to the chat! </span>
                    </p>
                    <p>
                        <span style={{color:`var(--br-c-normal)`}}>{"[renfe_lover]: "} </span>
                        <span style={{color:`white`}}>{"ivan putero"} </span>
                    </p>
                </div>
            </div>
        </div>
    );
}