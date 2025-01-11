import { useState, useEffect } from 'react';

function LeaveConfirmationModal({ showConfirmationModal, setShowConfirmationModal, handleLeave, game, user }) {
    const [isExiting, setIsExiting] = useState(false);

    const [isBlurred, setIsBlurred] = useState(false);

    const handleStay = () => {
        setIsExiting(true);
        setIsBlurred(false)
        setTimeout(() => {
            setShowConfirmationModal(false); 
            setIsExiting(false); 
        }, 1000); 
    };

    useEffect(() => {
        if (showConfirmationModal) {
            setIsBlurred(true);
            setIsExiting(false); 
        }
    }, [showConfirmationModal]);

    return (
        <>
            <div
                className={`confirmation-modal-overlay ${isBlurred ? 'blurredOverlay' : ''}`}
            ></div> 

            {showConfirmationModal && (
                <div className={`confirmation-modal ${isExiting ? 'popUp-exit' : ''}`}>
                    <h2 className="modal-title">Leave game</h2>
                    <p className="modal-text">
                        {game.gameState === 'WAITING' || game.spectators.some(spectator => spectator.user.username === user.username)
                            ? "Are you sure you want to leave?"
                            : "Leaving now will count as a loss, proceed?"
                        }
                    </p>
                    <div className="modal-buttons">
                        <button className="cancel-button" onClick={handleStay}>Stay</button>
                        <button className="confirm-button" onClick={handleLeave}>Leave</button>
                    </div>
                </div>
            )}
        </>
    );
}

export default LeaveConfirmationModal;