package es.us.dp1.lx_xy_24_25.your_game_name.game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;

@Controller
public class WSChatController {

    @Autowired
    GameService gs;
    @Autowired
    JwtUtils jwtu;

    @MessageMapping("/chat")
    @SendTo("/topic/chat") 
    public ChatMessage send(ChatMessage cm) {
        if(!jwtu.validateJwtToken(cm.getJwt())) throw new AccessDeniedException("You can't type in this chat room");
        gs.sendChatMessage(cm);
        return cm;
    }
}
