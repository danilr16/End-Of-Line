export default function MyGameModal({selectedGame, closeGameDataModal, parseGamemode, winLost}) {

    const modalClass = winLost === "WON" 
    ? "modal-win" 
    : winLost === "LOST" 
    ? "modal-lost" 
    : "modal-neutral";

    return (
        <div className="modal-overlay" onClick={closeGameDataModal}>
            <div 
                className={`modal-container-MyGames ${modalClass}`} 
                onClick={(e) => e.stopPropagation()}
            >
                <h2 className="modal-title">Game Details</h2>
                {selectedGame ? (
                    <div style={{ textAlign: 'left' }}>
                        <p style={{ marginTop: '20px' }}>
                            <strong>Game Mode:</strong> {parseGamemode}
                        </p>
                        <p>
                            <strong>Players:</strong>
                        </p>
                        {/* Contenedor para los jugadores */}
                        <div className="players-columns">
                            {selectedGame.players.map((player, index) => (
                                <div className="player-item" key={index}>
                                    <img 
                                        src={player.user.image} 
                                        alt="Player" 
                                        className="small-profile-image" 
                                    />
                                    <span>{player.user.username}</span>
                                </div>
                            ))}
                        </div>

                        <button className={`rounded-button ${modalClass}`} onClick={closeGameDataModal}> Final Game State </button>

                    </div>
                ) : (
                    <p>No game data available.</p>
                )}
            </div>
        </div>
    );
}