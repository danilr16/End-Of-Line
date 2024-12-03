import { useEffect, useState } from "react";
import tokenService from "../services/token.service"
import "../static/css/screens/Ranking.css"
import useFetchState from "../util/useFetchState";

export default function Ranking() {

    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [rankByVictories, setRankByVictories] = useState(true);

    const [user, setUser] = useFetchState(
        [],
        '/api/v1/currentUser',
        jwt,
        setMessage,
        setVisible
    )

    const [rankingStats, setRankingStats] = useFetchState(
        {},
        '/api/v1/statistics/ranking',
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

    const handleRankingChange = () => {
        if(rankByVictories) {
            setRankByVictories(false);
        } else {
            setRankByVictories(true);
        }
    };

    //Datos de prueba
    const mostVictories = { 'player1': 20, 'player2': 15, 'player3': 7, 'player4': 3, 'player5': 2, 'player6': 20, 'player7': 15, 'player8': 7, 'player9': 3, 'player10': 2 };
    const mostPoints = { 'player2': 1800, 'player4': 1200, 'player1': 900, 'player3': 500, 'player5': 300 };

    if (!rankingStats) {
        return <p>Loading...</p>;
    }
    
    return (
        <div className="main-container" key={rankByVictories}>
            <div className="upper-part">
                <span className="ranking-title">Ranking</span>
                <button className="ranking-switching" onClick={handleRankingChange}>View {!rankByVictories ? "victories ranking" : "points ranking"}</button>
            </div>
            
            <div className="podium">
                {rankByVictories ?
                    Object.entries(/*rankingStats.victories*/mostVictories).slice(0, 3).map(([key, value], index) => (
                        <div className={`podium-player-${index + 1}`} key={index}>{index + 1 + OrdinalValue(index + 1) + "\n"} {key + "\n"} {value} victories</div>
                    ))
                    :
                    Object.entries(/*rankingStats.points*/mostPoints).slice(0, 3).map(([key, value], index) => (
                        <div className={`podium-player-${index + 1}`} key={index}>{index + 1 + OrdinalValue(index + 1) + "\n"} {key + "\n"} {value} points</div>
                    ))
                }
            </div>
            <div className="below-3rd">
                {rankByVictories ?
                    Object.entries(/*rankingStats.victories*/mostVictories).slice(3).map(([key, value], index) => (
                        <div className="rank-player" key={index}>
                            <div className="name">{index + 4 + OrdinalValue(index + 4) + " "} {key}</div>
                            <div className="stats">{value} victories</div>
                        </div>
                    )) //Esto de añadir los espacios a mano como que no, busca luego una forma un poco mejor xd
                    :
                    Object.entries(/*rankingStats.points*/mostPoints).slice(3).map(([key, value], index) => (
                        <div className="rank-player" key={index}>
                            <div className="name">{index + 4 + OrdinalValue(index + 4) + " "} {key}</div>
                            <div className="stats">{value} points</div>
                        </div>
                    ))
                }
            </div>
        </div>
    )

}