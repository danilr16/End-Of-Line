package es.us.dp1.lx_xy_24_25.your_game_name.game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WSChatController {

    @Autowired
    GameService gs;
    
    @MessageMapping("/chat")
    @SendTo("/topic/chat") 
    public ChatMessage send(ChatMessage cm) {
        gs.sendChatMessage(cm);
        return cm;
    }
}
