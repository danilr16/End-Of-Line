package es.us.dp1.lx_xy_24_25.your_game_name.notifications;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.NotificationDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.Notification;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.NotificationService;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.NotificationType;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.WSNotificationController;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@ExtendWith(MockitoExtension.class)
public class WSNotificationControllerTests {
    
    @InjectMocks
    private WSNotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private Notification notification;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("user1");
        User sender = new User();
        sender.setId(2);
        sender.setUsername("sender1");

        notification = new Notification();
        notification.setId(1);
        notification.setGamecode("ABCDE");
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setUser(user);
        notification.setSender(sender);
        notification.setJwt("fake-jwt");
    }

    @Test
    void shouldNotThrowExceptionWhenValidToken() {
        when(jwtUtils.validateJwtToken(anyString())).thenReturn(true);
        when(userService.findUser(anyString())).thenReturn(user);
        when(notificationService.saveNotification(any(Notification.class))).thenReturn(notification);

        notificationController.sendNotification(notification.toDTO());
        verify(jwtUtils).validateJwtToken(anyString());
        verify(messagingTemplate).convertAndSend(anyString(), any(NotificationDTO.class));
    }

    @Test
    public void shouldThrowExceptionWhenInvalidToken() {
        when(jwtUtils.validateJwtToken(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> notificationController.sendNotification(notification.toDTO()),
            "You can't send notifications");
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(NotificationDTO.class));
    }
}
