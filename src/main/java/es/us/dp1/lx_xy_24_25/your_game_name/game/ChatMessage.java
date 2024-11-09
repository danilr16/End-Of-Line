package es.us.dp1.lx_xy_24_25.your_game_name.game;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ChatMessage {
    private String userName;
    private String messageString;

    ChatMessage(){}

    ChatMessage(String userName, String messageString){
        this.userName = userName;
        this.messageString = messageString;
    }
}
