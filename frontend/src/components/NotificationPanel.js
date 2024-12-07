import React, { forwardRef } from 'react';
import "../static/css/components/notificationPanel.css";

const NotificationPanel = forwardRef((props, ref) => {


    const parseToFRNotification = (n) => {
        return (
            <li className="notification" key={n.id}>
                <p><strong>{n.senderUsername}</strong> sent you a friend request</p>
                <div className="button-container">
                    <button className="accept-button">Accept</button>
                    <button className="reject-button">Reject</button>
                </div>
            </li>
        );
    };

    const notifications = props.notifications && props.notifications.length > 0 ? props.notifications.map((n) => parseToFRNotification(n)) : null;

    return (
        <section ref={ref} className="panel">
            <h3>Notifications</h3>
            {notifications ? <ul>{notifications}</ul> : <p style={{justifySelf:'center'}}>No notifications</p>}
        </section>
    );
});

export default NotificationPanel;