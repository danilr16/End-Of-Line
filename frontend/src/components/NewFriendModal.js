import React, { useState,useEffect } from 'react';
import "../static/css/components/modal.css";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import {useAlert} from "../AlertContext";
import request from '../util/request';

export default function NewFriendModal({friendName, setFriendName,closeModal,jwt,user}){
    const [loading, setLoading] = useState(false);
    const [client, setClient] = useState(null);
    const {updateAlert} = useAlert();


    useEffect(() => {
        const sock = new SockJS("http://localhost:8080/ws");
        const stompClient = new Client({
            webSocketFactory: () => sock,
            connectHeaders: {
                Authorization: `Bearer ${jwt}`, 
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

    const handleSubmit = async (e) => {
        e.preventDefault();
        const response = await request("/api/v1/users/friends","GET",null,jwt);
        const friends = Array.isArray(response.resContent)?response.resContent:[];
        if(friends.find(friend => friend.username === friendName)){
            updateAlert("You are already friends with this user");
            return;
        }
        if( friendName === user.username){
            updateAlert("You can't befriend yourself,socialize more");
            return;
        }
        const notification = {
            username: friendName,
            type: "FRIEND_REQUEST",
            senderUsername: user.username,
            gamecode:null,
            jwt: jwt
        }
        try{
            client.publish({
                destination: "/app/notifications",
                body: JSON.stringify(notification),
                });
                updateAlert("Friend request sent");
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