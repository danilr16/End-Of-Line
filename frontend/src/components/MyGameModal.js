import { useNavigate } from 'react-router-dom';

export default function MyGameModal({selectedGame, closeGameDataModal, parseGamemode, winLost}) {

    const modalClass = winLost === "WON" 
    ? "modal-win" 
    : winLost === "LOST" 
    ? "modal-lost" 
    : "modal-neutral";

    const modalClassButton = winLost === "WON" 
    ? "modal-bw" 
    : winLost === "LOST" 
    ? "modal-bl" 
    : "modal-neutral";

    const modalWL = winLost === "WON" 
    ? "modal-color-won" 
    : winLost === "LOST" 
    ? "modal-color-lost" 
    : "modal-neutral";

    const win_o_lost = winLost === "WON" 
    ? true
    : winLost === "LOST" ? false : null;

    const navigate = useNavigate();

    const handleButtonClick = () => {
        navigate(`/game/${selectedGame.gameCode}`);
    };
    

    return (
        <div className="modal-overlay" onClick={closeGameDataModal}>
            <div
                className={`modal-container-MyGames ${modalClass}`}
                onClick={(e) => e.stopPropagation()}
            >
                <h2 className="modal-title">Game Details</h2>
                <h3 className={"modal-title"}>{win_o_lost ? "You Won!" : "You Lost!"}</h3>
                {selectedGame ? (
                <>
                    <div style={{ textAlign: 'left' }}>
                    <p style={{ marginTop: '20px' }}>
                        <strong style={{color:'black' }}>Game Mode:</strong> {parseGamemode}
                    </p>
                    <p>
                        <strong style={{color:'black' }}>Players:</strong>
                    </p>
                    {/* Contenedor para los jugadores */}
                    <div className="players-columns">
                        {selectedGame.players.map((player, index) => (
                        <div className="player-item" key={index}>
                            <img
                            src={player.user.image}
                            alt="Player"
                            className="modal-image"
                            />
                            <span>{player.user.username}</span>
                        </div>
                        ))}
                    </div>
                    </div>
                    {/* Botón centrado */}
                    <div style={{ textAlign: 'center', marginTop: '20px' }}>
                    <button
                        className={`rounded-button ${modalClassButton}`}
                        onClick={handleButtonClick}
                    >
                        Final Game State
                    </button>
                    </div>
                </>
                ) : (
                    <p>No game data available.</p>
                )}
            </div>
        </div>
    );
}