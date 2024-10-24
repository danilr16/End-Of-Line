import React, { useState } from 'react';
import '../App.css';
import '../static/css/home/home.css';
import '../static/css/components/components.css';
import logo from '../static/images/palanca-de-mando.png';
import CreateModal from '../components/CreateModal';

export default function Home() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedGamemode, setSelectedGamemode] = useState('');
    const [maxPlayers, setMaxPlayers] = useState(2); 
    const [isPrivateRoom, setIsPrivateRoom] = useState(false); 

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

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
                <CreateModal
                    selectedGamemode={selectedGamemode}
                    setSelectedGamemode={setSelectedGamemode}
                    maxPlayers={maxPlayers}
                    setMaxPlayers={setMaxPlayers}
                    isPrivateRoom={isPrivateRoom}
                    setIsPrivateRoom={setIsPrivateRoom}
                    closeModal={closeModal}
                />
            )}
        </div>
    );
}