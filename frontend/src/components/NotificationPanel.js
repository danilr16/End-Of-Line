import React, { forwardRef } from 'react';
import "../static/css/components/notificationPanel.css";
import request from '../util/request';
import { useNavigate } from 'react-router-dom';


const NotificationPanel = forwardRef((props, ref) => {

        const navigate = useNavigate();
    


    const handleAcceptFriend = async (username) => {
        try {
            const response = await request(`/api/v1/users/addFriend/${username}`, 'PATCH', null, props.jwt);
            if (response.response.ok) {
                // Actualiza las notificaciones después de aceptar la solicitud de amistad
                //props.setNotifications((prevNotifications) => prevNotifications.filter(n => n.senderUsername !== username));
                console.log("Friend request accepted");
            } else {
                console.error("Error accepting friend request:", response.statusText);
            }
        } catch (error) {
            console.error("Error accepting friend request:", error);
        }
    };

    const handleAcceptGame = (n) => {
        navigate(`/game/${n.gamecode}`);
    }

    const parseNotification = (n,index) => {
        if (n.type === "FRIEND_REQUEST") {
            return parseToFRNotification(n,index);
        } else if (n.type === "GAME_INVITATION") {
            return parseToGINotification(n,index);
        }
    }

    const parseToFRNotification = (n,index) => {
        return (
            <li className="notification" key={index}>
                <p className = "notification-message"><strong>{n.senderUsername}</strong> sent you a friend request</p>
                <div className="notification-button-container">
                    <button className="reject-button">Reject</button>
                    <button className="accept-button" onClick={() => handleAcceptFriend(n.senderUsername)}>Accept</button>
                </div>
            </li>
        );
    };

    
    const parseToGINotification = (n,index) => {
        return (
            <li className="notification" key={index}>
                <p className = "notification-message"><strong>{n.senderUsername}</strong> wants you to join their game</p>
                <div className="notification-button-container">
                    <button className="reject-button">Reject</button>
                    <button className="accept-button" onClick={() => handleAcceptGame(n)}>Accept</button>
                </div>
            </li>
        );
    };

    const notifications = props.notifications && props.notifications.length > 0 ? props.notifications.map((n,index) => parseNotification(n,index)) : null;

    return (
        <section ref={ref} className="panel">
            <h3 className='notifications-title'>Notifications</h3>
            {notifications ? <ul className='notification-list'>{notifications}</ul> : <p style={{justifySelf:'center'}}>No notifications</p>}
        </section>
    );
});

export default NotificationPanel;