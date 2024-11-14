import React, { useState } from 'react';
import "../static/css/components/modal.css";
import { useNavigate } from 'react-router-dom';
import request from '../util/request';

export default function JoinGameModal({gameCode, setGameCode,closeModal,jwt}){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();


    const handleChange = (e) => {
        setGameCode(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (/^[A-Za-z]{5}$/.test(gameCode)) {
            const game =request(`/api/v1/games/${gameCode.toUpperCase()}`,"GET",null,jwt)
            if(game) navigate(`/game/${gameCode.toUpperCase()}`)
            else setError('That room does not exist');

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