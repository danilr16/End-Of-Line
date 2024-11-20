package es.us.dp1.lx_xy_24_25.your_game_name.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;

@SpringBootTest
@AutoConfigureTestDatabase
public class CardServiceTest {

    private CardService cardService;
    private PlayerService playerService;

    private Card mockCard;
    private Player examplePlayer;

    @Autowired
    private CardServiceTest(CardService cardService, PlayerService playerService){
        this.cardService = cardService;
        this.playerService = playerService;
    }

    @BeforeEach
    void setUpCard(){
        mockCard = new Card();
        examplePlayer = this.playerService.findPlayer(1);
        mockCard.setInput(1);
        mockCard.setOutputs(List.of(2,3));
        mockCard.setType(TypeCard.TYPE_1);
        mockCard.setPlayer(examplePlayer);
    }

    @Test
    void shouldFindAll(){
        List<Card> cards = (List<Card>) this.cardService.findAll();
        assertTrue(!cards.isEmpty());
    }

    @Test
    void shouldSaveCard(){
        Card savedCard = this.cardService.saveCard(mockCard);
        assertNotNull(savedCard.getId());
    }

    @Test
    void shouldFindCardById(){
        Card savedCard = this.cardService.saveCard(mockCard);
        Card foundCard = cardService.findCard(savedCard.getId());

        assertEquals(savedCard.getId(), foundCard.getId());
    }

    @Test
    void shouldUpdateCard(){
        Card cardToUpdate = this.cardService.saveCard(mockCard);
        
        cardToUpdate.setIniciative(2);
        cardToUpdate.setInput(2);
        cardToUpdate.setOutputs(List.of(3,0));

        Card updatedCard = this.cardService.updateCard(cardToUpdate, cardToUpdate.getId());
        assertEquals(updatedCard.getIniciative(), cardToUpdate.getIniciative());
        assertEquals(updatedCard.getInput(), cardToUpdate.getInput());
        assertEquals(updatedCard.getOutputs(), cardToUpdate.getOutputs());
    }
    
    @Test
    void shouldDeleteCard(){
        Card cardToDelete = this.cardService.saveCard(mockCard);

        this.cardService.deleteCard(cardToDelete.getId());
        assertThrows(ResourceNotFoundException.class, () -> this.cardService.findCard(cardToDelete.getId()));
    }

    @Test
    void shouldNotFindCardWithBadId(){
        Integer id = 99999;
        assertThrows(ResourceNotFoundException.class, () -> this.cardService.findCard(id));
    }
    
}
