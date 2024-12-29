import React, { useEffect, useState } from "react";
import request from "../util/request";
import tokenService from "../services/token.service";
import "../static/css/components/inviteFriendsList.css";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";


export default function InviteFriendsList({setShowModal,username,gamecode}) {

    const jwt = tokenService.getLocalAccessToken();
    const [friends, setFriends] = useState([]);
    const [client, setClient] = useState(null);

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
            achievementName:null,
            jwt: jwt
        }
        console.log(gamecode);
        try{
            client.publish({
                destination: "/app/notifications",
                body: JSON.stringify(notification),
                });
                console.log("Game Invitation sent");
        }
        catch(error){
            console.log("Error sending game invitation",error);
        }
    };

    const parseFriends = () => {
        return friends.map( (friend,index) => {
            return (
                <li className="invite-friend" key={index}>
                    <img src={friend.profileImg} alt="profile"/>
                    <section className="invite-friend-info">                 
                        <p>{friend.username}</p>
                        <svg onClick={() => {sendGameInvitation(friend);setShowModal(false)}} xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" >
                            <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 0 1-2.25 2.25h-15a2.25 2.25 0 0 1-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25m19.5 0v.243a2.25 2.25 0 0 1-1.07 1.916l-7.5 4.615a2.25 2.25 0 0 1-2.36 0L3.32 8.91a2.25 2.25 0 0 1-1.07-1.916V6.75" />
                        </svg>
                    </section>
                </li>
            );
        });
    }



    return(
        <ul className="invite-friends-list">
            <h3>Invite Friends</h3>
                <div className="invite-friends-list-content">
                    {parseFriends()}
                </div>
        </ul>
    );
}
