import { useEffect, useState } from "react";
import tokenService from "../services/token.service"
import "../static/css/screens/Ranking.css"
import useFetchState from "../util/useFetchState";

export default function Ranking() {

    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [rankByVictories, setRankByVictories] = useState(true);

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
        if (rankByVictories) {
            setRankByVictories(false);
        } else {
            setRankByVictories(true);
        }
    };

    if (!rankingStats || !rankingStats.victories || !rankingStats.points) {
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
                    rankingStats.victories.slice(0, 3).map((entry, index) => (
                        <div className={`podium-player-${index + 1}`} key={index}>{index + 1 + OrdinalValue(index + 1) + "\n"} {entry.userName + "\n"} {entry.victories} {entry.victories === 1 ? "victory" : "victories"}</div>
                    ))
                    :
                    rankingStats.points.slice(0, 3).map((entry, index) => (
                        <div className={`podium-player-${index + 1}`} key={index}>{index + 1 + OrdinalValue(index + 1) + "\n"} {entry.userName + "\n"} {entry.points} {entry.points === 1 ? "point" : "points"}</div>
                    ))
                }
            </div>
            <div className="below-3rd">
                {rankByVictories ?
                    rankingStats.victories.slice(3).map((entry, index) => (
                        <div className="rank-player" key={index}>
                            <div className="name">{index + 4 + OrdinalValue(index + 4) + " "} {entry.userName}</div>
                            <div className="stats">{entry.victories} {entry.victories === 1 ? "victory" : "victories"}</div>
                        </div>
                    ))
                    :
                    rankingStats.points.slice(3).map((entry, index) => (
                        <div className="rank-player" key={index}>
                            <div className="name">{index + 4 + OrdinalValue(index + 4) + " "} {entry.userName}</div>
                            <div className="stats">{entry.points} {entry.points === 1 ? "point" : "points"}</div>
                        </div>
                    ))
                }
            </div>
        </div>
    )

}