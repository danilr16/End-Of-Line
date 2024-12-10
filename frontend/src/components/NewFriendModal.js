import React, { useState,useEffect } from 'react';
import "../static/css/components/modal.css";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default function NewFriendModal({friendName, setFriendName,closeModal,jwt,user}){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [client, setClient] = useState(null);


    useEffect(() => {
        const sock = new SockJS("http://localhost:8080/ws");
        const stompClient = new Client({
            webSocketFactory: () => sock,
            connectHeaders: {
                Authorization: `Bearer ${jwt}`, // Agrega el token JWT aquí
            },
            onConnect: () => {
                console.log("Connected to WebSocket");
            },
            onStompError: (frame) => {
                console.error("STOMP error:", frame.headers["message"]);
                console.error("Details:", frame.body);
            },
        });
    
        stompClient.activate();
        setClient(stompClient);
    
        return () => stompClient.deactivate();
    }, [jwt]);

    const handleChange = (e) => {
        setFriendName(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const notification = {
            username: friendName,
            type: "FRIEND_REQUEST",
            senderUsername: user.username,
            gamecode:null,
            achievementName:null,
            jwt: jwt
        }
        try{
            client.publish({
                destination: "/app/notifications",
                body: JSON.stringify(notification),
                });
                console.log("Friend request sent");
        }
        catch(error){
            console.log("Error sending friend request",error);
        }
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