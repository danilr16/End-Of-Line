package es.us.dp1.lx_xy_24_25.your_game_name.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Optional<List<Notification>> findByUser(User user) {
        return notificationRepository.findByUser(user);
    }
    public Optional<List<Notification>> findByUserSender(User user, User sender) {
        return notificationRepository.findByUserSender(user, sender);
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }
}