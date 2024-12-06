package es.us.dp1.lx_xy_24_25.your_game_name.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;

@Controller
public class WSNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/notifications")
    public void sendNotification( Notification notification) {
        if (!jwtUtils.validateJwtToken(notification.getJwt())) {
            throw new AccessDeniedException("You can't send notifications");
        }
        notificationService.saveNotification(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUser().getUsername(), notification);
    }
}
