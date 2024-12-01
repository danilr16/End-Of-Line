import React, { forwardRef } from "react";
import "../static/css/components/notificationPanel.css"

const NotificationPanel = forwardRef((props, ref) => {
return (
    <section ref={ref} className="panel">
        <h3>Notificaciones</h3>
        <ul>
            <li>Notificación 1</li>
            <li>Notificación 2</li>
            <li>Notificación 3</li>
        </ul>
    </section>
);
});


export default NotificationPanel;


