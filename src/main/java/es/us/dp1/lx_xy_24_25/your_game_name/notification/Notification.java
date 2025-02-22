package es.us.dp1.lx_xy_24_25.your_game_name.notification;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.NotificationDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    
    @Enumerated(EnumType.STRING)
    NotificationType type;

    @ManyToOne
    User sender;
    String gamecode;

    String jwt;

    public Notification() {
    }

    public Notification(User user,NotificationType type, User sender, String gamecode) {
        this.user = user;
        this.type = type;
        this.sender = sender;
        this.gamecode = gamecode;
    }

    
    public NotificationDTO toDTO() {
        return new NotificationDTO(
            this.user.getUsername(),
            this.type.toString(),
            this.sender != null ? this.sender.getUsername() : null,
            this.gamecode,
            this.jwt
        );
    }

    public static Notification fromDTO(NotificationDTO dto, NotificationType type, User user, User sender) {
        return new Notification(
            user,
            type,
            sender,
            dto.getGamecode()
        );
    }
    
}
