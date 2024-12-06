import React, { useState } from 'react';
import "../static/css/components/modal.css";
import { useNavigate } from 'react-router-dom';
import request from '../util/request';

export default function NewFriendModal({friendName, setFriendName,closeModal,jwt}){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();


    const handleChange = (e) => {
        setFriendName(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        request('/api/v1/users/addFriend',"PATCH",{username:friendName},jwt)
    };

    return (
        <div className='modal-overlay'>
            <div className='modal-container'>
                <h2>Add a friend</h2>
                <form onSubmit={handleSubmit}>
                    <div className='form-group'>
                    <label htmlFor="friendName">Friend Name:</label>
                    <input
                        type="text"
                        id="friendName"
                        name="friendName"
                        value={friendName}
                        onChange={handleChange}
                        required
                    />
                    </div>
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
                            Add Friend
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}