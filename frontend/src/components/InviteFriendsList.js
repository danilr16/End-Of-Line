import React, { useEffect, useState } from "react";
import request from "../util/request";
import tokenService from "../services/token.service";
import "../static/css/components/inviteFriendsList.css";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useAlert } from "../AlertContext";


export default function InviteFriendsList({setShowModal,username,gamecode}) {

    const jwt = tokenService.getLocalAccessToken();
    const [friends, setFriends] = useState([]);
    const [client, setClient] = useState(null);
    const {alertMessage,updateAlert} = useAlert();

    useEffect( () => {
        async function fetchFriends() {
            if(!jwt) return;
            try {
                const response = await request("/api/v1/users/friends","GET",null,jwt);
                setFriends(Array.isArray(response.resContent)?response.resContent:[]);
            } catch (error) {
                console.error("Error fetching friends:", error);
            }
        }
        fetchFriends();
    }, [jwt]);

    
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

    const sendGameInvitation = (friend) => {
        const notification = {
            username: friend.username,
            type: "GAME_INVITATION",
            senderUsername: username,
            gamecode:gamecode,
            jwt: jwt
        }
        console.log(gamecode);
        try{
            client.publish({
                destination: "/app/notifications",
                body: JSON.stringify(notification),
                });
            updateAlert(`${friend.username} was invited to this game`);
            console.log(alertMessage);
        }
        catch(error){
            console.log("Error sending game invitation",error);
        }
    };

    const parseFriends = () => {
        return friends.map( (friend,index) => {
            return (
                <li className="invite-friend" key={index} onClick={() => {sendGameInvitation(friend);setShowModal(false)}}>
                    <img src={friend.profileImg} alt="profile"/>
                    <section className="invite-friend-info" >                 
                        <p>{friend.username}</p>
                    </section>
                </li>
            );
        });
    }

    return(
        <ul className="invite-friends-list">
            <div className="invite-friends-list-header">
                <h3>Invite Friends</h3>
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="4" stroke="currentColor" className="close-icon" onClick={() => setShowModal(false)}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M6 18 18 6M6 6l12 12" />
                </svg>
            </div>

                <div className="invite-friends-list-content">
                    {parseFriends()}
                </div>
        </ul>
    );
}
