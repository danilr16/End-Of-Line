import { useState } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import "../static/css/screens/GlobalStats.css";


export default function IndividualStats() {

    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);

    const [user, setUser] = useFetchState(
        [],
        '/api/v1/users/currentUser',
        jwt,
        setMessage,
        setVisible
    );

    const [gameModeStats, setGameModeStats] = useFetchState(
        {},
        '/api/v1/statistics/players',
        jwt,
        setMessage,
        setVisible
    )

    const [durationStats, setDurationStats] = useFetchState(
        {},
        '/api/v1/statistics/time',
        jwt,
        setMessage,
        setVisible
    )

    const [gameStats, setGameStats] = useFetchState(
        {},
        '/api/v1/statistics/games',
        jwt,
        setMessage,
        setVisible
    )

    const OrdinalValue = (number) => {
        switch (number) {
            case 1: return 'st'
            case 2: return 'nd'
            case 3: return 'rd'
            default: return 'th'
        }
    }
    //Datos de prueba
    console.log(gameStats)
    const mostPlayedModes = { 'versus': 247, 'singleplayer': 134 }


    if (!gameModeStats || !durationStats.global || !durationStats.user || !gameStats) {
        return <p> Loading... </p>;
    }

    return (
        <div className="main-container">
            <div className="screen-title">
                Global Stats
            </div>
            <span className="games-duration">Games duration</span>
            <div className="tables-container">
                <div className="longest-game-container">
                    <table>
                        <thead><th>Longest game</th></thead>
                        <tbody>
                            <tr><td>Global: {durationStats.global.max} minutes</td></tr>
                            <tr><td>Individual: {durationStats.user.max} minutes</td></tr>
                        </tbody>
                    </table>
                </div>
                <div className="shortest-game-container">
                    <table>
                        <thead><th>Shortest game</th></thead>
                        <tbody>
                            <tr><td>Global: {durationStats.global.min} minutes</td></tr>
                            <tr><td>Individual: {durationStats.user.min} minutes</td></tr>
                        </tbody>
                    </table>
                </div>
                <div className="total-games-container">
                    <table>
                        <thead><th>Total games</th></thead>
                        <tbody>
                            <tr><td>Global: {gameStats.global.total}</td></tr>
                            <tr><td>Individual: {gameStats.user.total}</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <span className="popular-gamemodes">Popular gamemodes</span>
            <div className="popular-gamemodes-container">
                {Object.entries(/*gameModeStats.globalMostPlayed*/mostPlayedModes).map(([key, value], index) => (
                    <div className="gamemode-container" key={index}> {index + 1 + OrdinalValue(index + 1) + "\n"} {key + "\n"} {value} games </div>
                ))}
            </div>

        </div>
    )
}