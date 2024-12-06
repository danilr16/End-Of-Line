package es.us.dp1.lx_xy_24_25.your_game_name.notification;

import es.us.dp1.lx_xy_24_25.your_game_name.achievements.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appnotifications")
public class Notification extends BaseEntity {


    @ManyToOne
    @NotNull
    User user;
    
    NotificationType type;

    @ManyToOne
    User sender;
    String gamecode;
    @ManyToOne
    Achievement achievement;

    String jwt;

    public Notification() {
    }

    public Notification(User user,NotificationType type, User sender, String gamecode, Achievement achievement) {
        this.user = user;
        this.type = type;
        this.sender = sender;
        this.gamecode = gamecode;
        this.achievement = achievement;
    }
    
}
