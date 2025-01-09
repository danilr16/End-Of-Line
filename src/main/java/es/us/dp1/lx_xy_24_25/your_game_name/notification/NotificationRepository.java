package es.us.dp1.lx_xy_24_25.your_game_name.notification;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;


public interface NotificationRepository extends CrudRepository<Notification, Integer> {
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user")
    Optional<List<Notification>> findByUser(User user);

    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.sender = :sender")
    Optional<List<Notification>> findByUserSender(User user, User sender);

    @Query("SELECT n FROM Notification n WHERE n.user = :user OR n.sender = :user")
    Optional<List<Notification>> findAllByUser(User user);
}
