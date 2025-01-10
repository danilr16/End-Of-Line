import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function GameResultModal({ showResultModal, setShowResultModal, won }) {
    const [isExiting, setIsExiting] = useState(false);
    const [isBlurred, setIsBlurred] = useState(false);

    const navigate = useNavigate();
    
    const handleStay = () => {
        setIsExiting(true);
        setIsBlurred(false);
        setTimeout(() => {
            setShowResultModal(false);
            setIsExiting(false);
        }, 1000);
    };

    const handleLeave = () => {
        navigate('/');  // Navigate to root path
    };

    useEffect(() => {
        if (showResultModal) {
            setIsBlurred(true);
            setIsExiting(false);
        }
    }, [showResultModal]);

    return (
        <>
            <div
                className={`confirmation-modal-overlay ${isBlurred ? 'blurredOverlay' : ''}`}
            ></div>

            {showResultModal && (
                <div className={`confirmation-modal ${isExiting ? 'popUp-exit' : ''}`}>
                    <h2 className="modal-title">
                        {won ? 'You Won!' : 'You Lost!'}
                    </h2>
                    <p className="modal-text">
                        {won
                            ? "Congratulations!"
                            : "Better luck next time!"}
                    </p>
                    <div className="modal-buttons">
                        <button className="cancel-button" onClick={handleStay}>Stay</button>
                        <button className="confirm-button" onClick={handleLeave}>Leave</button> {/* Changed here */}
                    </div>
                </div>
            )}
        </>
    );
}

export default GameResultModal;