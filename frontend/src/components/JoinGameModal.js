import React, { useState } from 'react';
import "../static/css/components/components.css";
import tokenService from "../services/token.service"

export default function JoinGameModal({gameCode, setGameCode,closeModal}){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);


    const handleChange = (e) => {
        setGameCode(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (/^[A-Za-z]{5}$/.test(gameCode)) {
            console.log('Game Code submitted:', gameCode);
            setError('To be implemented.');

        } else {
            setError('Please enter exactly 5 letters.');
        }
    };

    return (
        <div className='modal-overlay'>
            <div className='modal-container'>
                <h2>Join a Room</h2>
                <form onSubmit={handleSubmit}>
                    <div className='form-group'>
                    <label htmlFor="gameCode">Code:</label>
                    <input
                        type="text"
                        id="gameCode"
                        name="gameCode"
                        value={gameCode}
                        onChange={handleChange}
                        maxLength="5"
                        pattern="[A-Za-z]{5}"
                        required
                        title="Please enter exactly 5 letters."
                    />
                    </div>
                    {error && <div className="error-message">{error}</div>}
                    <div className="modal-buttons">
                        <button 
                            type="button" 
                            className="big-button" 
                            style={{ backgroundColor: "#adadad", color: "#000000" }} 
                            onClick={closeModal}
                        >
                            Cancel
                        </button>
                        <button type="submit" className="big-button" disabled={loading}>
                            {loading ? 'Joining...' : 'Join'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}