package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {
    @NotNull
    private String username;
    private String type;
    private String senderUsername;
    private String gamecode;
    private String jwt;

    public NotificationDTO() {
    }

    public NotificationDTO(String username, String type, String senderUsername, String gamecode,  String jwt) {
        this.username = username;
        this.type = type;
        this.senderUsername = senderUsername;
        this.gamecode = gamecode;
        this.jwt = jwt;
    }
}