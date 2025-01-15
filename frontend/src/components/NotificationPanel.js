import React, { forwardRef } from 'react';
import "../static/css/components/notificationPanel.css";
import request from '../util/request';
import { useNavigate } from 'react-router-dom';
import { useAlert } from '../AlertContext';


const NotificationPanel = forwardRef((props, ref) => {

        const navigate = useNavigate();
        const {updateAlert} = useAlert();
    


    const handleAcceptFriend = async (n) => {
        console.log(n);
        try {
            const response = await request(`/api/v1/users/addFriend/${n.senderUsername}`, 'PATCH', null, props.jwt);
            if (response.response.ok) {
                props.setNotifications((prevNotifications) => prevNotifications
                        .filter(not => not.senderUsername !== n.senderUsername));
                updateAlert("Friend request accepted");
                await request(`/api/v1/notifications`, 'DELETE', n, props.jwt);
            } else {
                console.error("Error accepting friend request:", response.statusText);
            }
        } catch (error) {
            console.error("Error accepting friend request:", error);
        }
    };

    const handleAcceptGame = async (n) => {
        props.setNotifications((prevNotifications) => prevNotifications
            .filter(not => not.senderUsername !== n.senderUsername));
        navigate(`/game/${n.gamecode}`);
        await request(`/api/v1/notifications`, 'DELETE', n, props.jwt);

    }

    const handleReject = async(n) =>{
        props.setNotifications((prevNotifications) => prevNotifications
            .filter(not => not.senderUsername !== n.senderUsername));
        await request(`/api/v1/notifications`, 'DELETE', n, props.jwt);

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
                    <button className="reject-button" onClick={() => handleReject(n)}>Reject</button>
                    <button className="accept-button" onClick={() => handleAcceptFriend(n)}>Accept</button>
                </div>
            </li>
        );
    };

    
    const parseToGINotification = (n,index) => {
        return (
            <li className="notification" key={index}>
                <p className = "notification-message"><strong>{n.senderUsername}</strong> wants you to join their game</p>
                <div className="notification-button-container">
                    <button className="reject-button" onClick ={() => handleReject(n)}>Reject</button>
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