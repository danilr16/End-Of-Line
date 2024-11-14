import "../static/css/components/inGamePlayerList.css"

export default function InGamePlayerList({players}){

    return(
        <div className="player-list-container">
                    <ul className="player-list">
                        <h5 style={{ color: "white" }}>
                            Players:
                        </h5>

                        {players.map((player, index) => (
                            <div className="player-container" key={index}>
                                <div>
                                    <p className="player-container-text">{player.user.username}</p>
                                </div>
                            </div>
                        ))}

                    </ul>
                </div>
    );

}