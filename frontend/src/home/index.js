import React, { useState } from 'react';
import '../App.css';
import '../static/css/home/home.css';
import '../static/css/components/components.css';
import logo from '../static/images/palanca-de-mando.png' 

export default function Home(){
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedGamemode, setSelectedGamemode] = useState('');
    const [maxPlayers, setMaxPlayers] = useState(2); 
    const [isPrivateRoom, setIsPrivateRoom] = useState(false); 

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

    const handleCreateRoom = (e) => {
        e.preventDefault();
        console.log('Gamemode Selected: ', selectedGamemode);
        console.log('Max Players: ', maxPlayers);
        console.log('Private Room: ', isPrivateRoom);
        closeModal(); 
    };

    const getMaxPlayerOptions = () => {
        switch (selectedGamemode) {
            case 'versus':
            case 'team-battle':
                return [2, 3, 4, 5, 6, 7, 8]; 
            case 'puzzle-coop':
                return [2, 3, 4]; 
            case 'classic-single':
                return [1];
            default:
                return []; 
        }
    };

    return (
        <div>
            <div className={`home-page-container ${isModalOpen ? 'blurred' : ''}`}>
                <div className="hero-div">
                    <h1>End of Line</h1>
                    <div className="button-container">
                        <button className="big-button" onClick={openModal}>Create</button>
                        <button className="big-button">Join</button>
                    </div>
                </div>
            </div>

            {/* Modal */}
            {isModalOpen && (
                <div className="createModal-overlay">
                    <div className="createModal">
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
                                    <option value="puzzle-coop">Puzzle</option>
                                    <option value="classic-single">Single Player</option>
                                    <option value="team-battle">Team Battle</option>
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
                                    <label>
                                        Private Room
                                    </label>
                                </div>
                                <div className="form-group">
                                    <input 
                                        type="checkbox" 
                                        checked={isPrivateRoom} 
                                        onChange={(e) => setIsPrivateRoom(e.target.checked)} 
                                    />
                                </div>
                            </div>

                            <div className="createModal-buttons">
                                
                                <button type="button" className="big-button" style={{backgroundColor:"#adadad",color:"#000000"}} onClick={closeModal}>Cancel</button>
                                <button type="submit" className="big-button">Create</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};