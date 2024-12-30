package es.us.dp1.lx_xy_24_25.your_game_name.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
public class WSChatControllerTests {

    @InjectMocks
    private WSChatController chatController;

    @Mock
    private GameService gameService;

    @Mock
    private JwtUtils jwtUtils;

    private ChatMessage cm;

    @BeforeEach
    void setUp() {
        cm = new ChatMessage();
        cm.setJwt("fake-jwt");
        cm.setMessageString("This is a test message");
    }

    @Test
    public void shouldNotThrowExceptionWhenValidToken() throws Exception {
        
        //Se simula un token válido
        when(jwtUtils.validateJwtToken(cm.getJwt())).thenReturn(true);

        ChatMessage sentMessage = chatController.send(cm);

        assertEquals("This is a test message", sentMessage.getMessageString());

        //Se verifica que se hace la llamada desde el controlador a gameService
        verify(gameService).sendChatMessage(cm);
    }

    @Test
    public void shouldThrowExceptionWhenInvalidToken() throws Exception {
        
        //Se simula un token inválido
        when(jwtUtils.validateJwtToken(cm.getJwt())).thenReturn(false);

        
        assertThrows(AccessDeniedException.class, () -> chatController.send(cm));
        
        // Se verifica que no se llame a gameService, ya que la excepción debería saltar antes de hacer la llamada
        verify(gameService, never()).sendChatMessage(any(ChatMessage.class));
    }
}
