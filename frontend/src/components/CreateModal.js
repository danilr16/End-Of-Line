import React, { useState } from 'react';
import "../static/css/components/components.css";
import tokenService from "../services/token.service"
import { useNavigate } from 'react-router-dom';


export default function CreateModal({
    selectedGamemode,
    setSelectedGamemode,
    maxPlayers,
    setMaxPlayers,
    isPrivateRoom,
    setIsPrivateRoom,
    closeModal
}) {

    const token = tokenService.getLocalAccessToken();

    const navigate = useNavigate();

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleCreateRoom = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        const payload = {
            numPlayers: maxPlayers,
            gameMode: selectedGamemode.toUpperCase(),
            isPublic: !isPrivateRoom
        };

        try {
            const response = await fetch('/api/v1/games', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error('Failed to create game');
            }

            const data = await response.json();
            console.log('Game created successfully:', data);

            closeModal();

            navigate(`/games/${data.gameCode}`)
        } catch (err) {
            console.error(err);
            setError('An error occurred while creating the game.');
        } finally {
            setLoading(false);
        }
    };

    const getMaxPlayerOptions = () => {
        switch (selectedGamemode) {
            case 'versus':
            case 'team_battle':
                return [2, 3, 4, 5, 6, 7, 8]; 
            case 'puzzle_coop':
                return [2, 3, 4]; 
            case 'puzzle_single':
                return [1];
            default:
                return []; 
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <h2>Create a Room</h2>
                <form onSubmit={handleCreateRoom}>
                    <div className="form-group">
                        <label>Gamemode:</label>
                        <select 
                            value={selectedGamemode} 
                            onChange={(e) => setSelectedGamemode(e.target.value)} 
                            required
                        >
                            <option value="" disabled>Select a Gamemode</option>
                            <option value="versus">Versus</option>
                            <option value="puzzle_coop">Puzzle Coop</option>
                            <option value="puzzle_single">Single Player</option>
                            <option value="team_battle">Team Battle</option>
                        </select>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Max Players:</label>
                            <select 
                                value={maxPlayers} 
                                onChange={(e) => setMaxPlayers(Number(e.target.value))} 
                                required
                            >
                                {getMaxPlayerOptions().map((num) => (
                                    <option key={num} value={num}>{num}</option>
                                ))}
                            </select>
                        </div>
                        <div className="form-group">
                            <label>Private Room:</label>
                        </div>
                        <div className="form-group">
                            <input 
                                type="checkbox" 
                                checked={isPrivateRoom} 
                                onChange={(e) => setIsPrivateRoom(e.target.checked)} 
                            />
                        </div>
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
                            {loading ? 'Creating...' : 'Create'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}