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

    const OrdinalValue = (number) => {
        switch (number) {
            case 1: return 'st'
            case 2: return 'nd'
            case 3: return 'rd'
            default: return 'th'
        }
    }

    //datos de prueba para poderes mas usados y modos mas jugados (vienen en forma de map, cuidado)
    const mostPlayedModes = ["Team battle", "Versus", "Singleplayer", "Co-op"]
    const mostUsedPowers = ["Accelerate", "Back away"]

    return (
        <div className="main-container">
            <div className="screen-title">
                Individual Stats
            </div>
            <div className="games-stats">
                <div className="average-points">
                    Average points:
                </div>
                <div className="max-points-obtained">
                    Maximum points obtained:
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
                            {mostPlayedModes.map((mode, index) => (
                                <tr>
                                    <td key={index}>{index + 1 + OrdinalValue(index + 1)}       {mode}</td>
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
                                {mostUsedPowers.map((power, index) => (
                                    <td key={index}>{index + 1 + OrdinalValue(index + 1) + "\n"} {power + "\n"} 35 uses</td>
                                ))}
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )

}