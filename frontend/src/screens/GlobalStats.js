import { useState } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import "../static/css/screens/GlobalStats.css";


export default function IndividualStats() {

    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);

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

    if (!gameModeStats || !durationStats.global || !durationStats.user || !gameStats || !gameStats.global) {
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

            <span className="popular-gamemode">Most popular gamemode</span>
            <div className="popular-gamemode-container">
                <div className="gamemode-container"> {String(gameModeStats.globalMostPlayed).replace("_", " ")} </div>
            </div>

        </div>
    )
}