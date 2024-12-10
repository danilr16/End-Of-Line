package es.us.dp1.lx_xy_24_25.your_game_name.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import es.us.dp1.lx_xy_24_25.your_game_name.achievements.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.NotificationDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@Controller
public class WSNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notifications")
    public void sendNotification(NotificationDTO notificationDTO) {
        System.out.println("Received notification: " + notificationDTO);
        if (!jwtUtils.validateJwtToken(notificationDTO.getJwt())) {
            throw new AccessDeniedException("You can't send notifications");
        }

        NotificationType type = NotificationType.valueOf(notificationDTO.getType());
        User user = userService.findUser(notificationDTO.getUsername());
        User sender = notificationDTO.getSenderUsername() != null ? userService.findUser(notificationDTO.getSenderUsername()) : null;
        Achievement achievement = null; // Fetch achievement based on notificationDTO.getAchievementName() if needed

        Notification notification = Notification.fromDTO(notificationDTO, type, user, sender, achievement);
        notificationService.saveNotification(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + notificationDTO.getUsername(), notificationDTO);
    }
}