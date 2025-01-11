package es.us.dp1.lx_xy_24_25.your_game_name.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.notification.Notification;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.NotificationService;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.NotificationType;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@SpringBootTest
@AutoConfigureTestDatabase
public class NotificationServiceTests {
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Test
    void shouldFindByUser() {
        User user = userService.findUser(4);
        List<Notification> res = notificationService.findByUser(user).get();
        assertEquals(1, res.size());
        assertEquals("admin1", res.stream().findFirst().get().getSender().getUsername());
        assertEquals("player1", res.stream().findFirst().get().getUser().getUsername());
    }

    @Test
    void shouldFindByUserSender() {
        User user = userService.findUser(4);
        User sender = userService.findUser(1);
        List<Notification> res = notificationService.findByUserSender(user, sender).get();
        assertEquals(1, res.size());
        assertEquals("admin1", res.stream().findFirst().get().getSender().getUsername());
        assertEquals("player1", res.stream().findFirst().get().getUser().getUsername());
    }

    @Test
    void shouldFindAllByUser() {
        User user = userService.findUser(4);
        List<Notification> notifications = notificationService.findAllByUser(user).get();
        assertEquals(1, notifications.size());
    }

    @Test
    void shouldCreateNotification() {
        User user = userService.findUser(4);
        User sender = userService.findUser(1);
        Notification notification = new Notification();
        notification.setGamecode("ABCDE");
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setUser(user);
        notification.setSender(sender);

        notificationService.saveNotification(notification);
        assertNotNull(notification.getId());
        assertEquals(2, notificationService.findByUser(user).get().size());
        notificationService.deleteNotification(notification.getId());
    }

    @Test
    void shouldDeleteNotification() {
        User user = userService.findUser(4);
        User sender = userService.findUser(1);
        Notification notification = new Notification();
        notification.setGamecode("ABCDE");
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setUser(user);
        notification.setSender(sender);
        notificationService.saveNotification(notification);
        
        Integer cont = notificationService.findAllByUser(user).get().size();
        notificationService.deleteNotification(1);
        Integer newCont = notificationService.findAllByUser(user).get().size();
        assertEquals(cont -1, newCont);
    }
}
