import jwt_decode from "jwt-decode";
import { useDebugValue, useEffect, useState, useRef } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { useParams } from "react-router-dom";
import { ColorProvider,useColors } from "../ColorContext";

export default function GameScreen() {
    const jwt = tokenService.getLocalAccessToken();
    const [gridSize, setGridSize] = useState(7); 

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
                updateColors({
                    light: 'var(--br-green-light)',
                    normal: 'var(--br-green)',
                    dark: 'var(--br-green-dark)',
                });
                break;
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
            <div className="table-frame" style={{width:`${(gridSize/5)*5+20}%`}}>
                <div 
                    className="grid-container" 
                    ref={gridRef} 
                    style={{ 
                        gridTemplateColumns: `repeat(${gridSize}, 1fr)`, 
                        gridTemplateRows: `repeat(${gridSize}, 1fr)` ,
                        gap:`${(5/gridSize)*2}%`
                    }}
                >
                    {Array.from({ length: gridSize * gridSize }, (_, index) => (
                        <div key={index} className="grid-item" ></div>
                    ))}
                </div>
                
            </div>
        </div>
    );
}