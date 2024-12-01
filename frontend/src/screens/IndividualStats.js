import { useState } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import "../static/css/screens/IndividualStats.css";


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

    const [userScoreStats, setUserScoreStats] = useFetchState(
        {},
        '/api/v1/statistics/games',
        jwt,
        setMessage,
        setVisible
    )

    const [gameModeStats, setGameModeStats] = useFetchState(
        {},
        '/api/v1/statistics/players',
        jwt,
        setMessage,
        setVisible
    )

    const [powersStats, setPowersStats] = useFetchState(
        {},
        '/api/v1/statistics/myGames',
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
    const mostPlayedModes = {'versus': 247, 'singleplayer': 134}
    const mostUsedPowers = {'accelerate': 385, 'back away': 264}
    

    if(!userScoreStats || !gameModeStats || !userScoreStats.user || !powersStats) {
        return <p> Loading... </p>;
    }

    return (
        <div className="main-container">
            <div className="screen-title">
                Individual Stats
            </div>
            <div className="games-stats">
                <div className="average-points">
                    Average points: {userScoreStats.user.average}
                </div>
                <div className="max-points-obtained">
                    Maximum points obtained: {userScoreStats.user.max}
                </div>
                <div className="longest-victory-streak">
                    Longest victory streak:
                </div>
            </div>
            <div className="tables">
                <div className="most-played-modes-container">
                    <table className="most-played-modes">
                        <thead><th>Most played gamemodes</th></thead>
                        <tbody>
                            {Object.entries(/*gameModeStats.userMostPlayed*/mostPlayedModes).map(([key, value], index) => (
                                <tr>
                                    <td key={index}>{index + 1 + OrdinalValue(index + 1)} {key} {value}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <div className="most-used-powers-container">
                    <table className="most-used-powers">
                        <thead><th colSpan="2">Most used powers</th></thead>
                        <tbody>
                            <tr>
                                {Object.entries(/*powersStats.powersMostUsed*/mostUsedPowers).map(([key, value], index) => (
                                    <td key={index}>{index + 1 + OrdinalValue(index + 1) + "\n"} {key + "\n"} {value} uses</td>
                                ))}
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )
}