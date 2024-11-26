package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.beans.factory.annotation.Autowired;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ChatMessage {
    private String jwt;
    private String messageString;
    private String gameCode;
    private String userName;


    ChatMessage(){}

    ChatMessage(String userName,String jwt, String messageString, String gameCode){
        this.userName = userName;
        this.jwt = jwt;
        this.messageString = messageString;
        this.gameCode = gameCode;
    }
}
