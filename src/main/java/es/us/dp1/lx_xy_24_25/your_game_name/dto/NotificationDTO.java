package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {
    private String username;
    private String type;
    private String senderUsername;
    private String gamecode;
    private String achievementName;
    private String jwt;

    public NotificationDTO() {
    }

    public NotificationDTO(String username, String type, String senderUsername, String gamecode, String achievementName, String jwt) {
        this.username = username;
        this.type = type;
        this.senderUsername = senderUsername;
        this.gamecode = gamecode;
        this.achievementName = achievementName;
        this.jwt = jwt;
    }
}