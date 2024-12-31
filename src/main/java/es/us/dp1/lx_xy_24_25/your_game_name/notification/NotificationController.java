package es.us.dp1.lx_xy_24_25.your_game_name.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.NotificationDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(@PathVariable String userName) {
        User user = userService.findUser(userName);
            Optional<List<Notification>> notifications = notificationService.findByUser(user);
            if (notifications.isPresent()) {
                List<NotificationDTO> ndto = notifications.get().stream().map(notification -> notification.toDTO()).toList();
                return new ResponseEntity<>(ndto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody @Valid Notification notification) {
        Notification savedNotification = notificationService.saveNotification(notification);
        return new ResponseEntity<>(savedNotification, HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteNotification(Integer id) {
        System.out.println("Deleted");
        notificationService.deleteNotification(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteNotificationBySecondaryKey(@RequestBody @Valid NotificationDTO notificationDTO) {
        System.out.println("deleteNotificationBySecondaryKey called");
        User user= userService.findUser(notificationDTO.getUsername());
        User sender = userService.findUser(notificationDTO.getSenderUsername());
        List<Notification> nots = notificationService.findByUserSender(user,sender).orElse(List.of());
        System.out.println(nots);
        if(!nots.isEmpty()) nots.stream().forEach( n-> {System.out.println("Deleting " + n.getId()); deleteNotification(n.getId());});
        else throw new ResourceNotFoundException("Notifications not found");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}