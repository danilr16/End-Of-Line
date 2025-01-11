package es.us.dp1.lx_xy_24_25.your_game_name.player;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
public class playerServiceTests {
    @Autowired 
    private PlayerService playerService;
    @Autowired 
    private UserService userService;
    @Autowired
    private PackCardService packCardService;
    @Autowired
    private HandService handService;


    @Test
    void shouldFindAll() {
        List<Player> players = (List<Player>)playerService.findAll();
        assertTrue(!players.isEmpty());
        assertTrue(players.size() >= 1);
    }
    @Test
    void shouldFindById() {
        Player player = playerService.findPlayer(1);
        assertEquals(PlayerState.WON, player.getState());
    }
    @Test
    void shouldNotFindById() {
        assertThrows(ResourceNotFoundException.class, () -> playerService.findPlayer(9999));
    }
      List<Card> simCreate25Cards(Player player) { 
        List<Card> cards = new ArrayList<>();
        for(int i=1;i<=3;i++) {
            Card c1 = Card.createByType(TypeCard.TYPE_1, player);
            cards.add(c1);
            Card c2 = Card.createByType(TypeCard.TYPE_2_IZQ, player);
            cards.add(c2);
            Card c3 = Card.createByType(TypeCard.TYPE_2_DER, player);
            cards.add(c3);
            Card c4 = Card.createByType(TypeCard.TYPE_3_IZQ, player);
            cards.add(c4);
            Card c5 = Card.createByType(TypeCard.TYPE_3_DER, player);
            cards.add(c5);
            Card c6 = Card.createByType(TypeCard.TYPE_4, player);
            cards.add(c6);
            Card c7 = Card.createByType(TypeCard.TYPE_5, player);
            cards.add(c7);
            Card c8 = Card.createByType(TypeCard.TYPE_0, player);
            cards.add(c8);
        }
        Card c9 = Card.createByType(TypeCard.TYPE_1, player);
        cards.add(c9);
        return cards;
    }

    @Test
    void shouldSavePlayer() {
        User user = this.userService.findUser(4);

        Player p = new Player();
        p.setEnergy(3);
        p.setUser(user);

        PackCard pc = new PackCard();
        List<Card> cards = simCreate25Cards(p);
        pc.setCards(cards);
        pc.setNumCards(cards.size());
        packCardService.savePackCard(pc);

        List<PackCard> packCards = new ArrayList<>();
        packCards.add(pc);
        p.setPackCards(packCards);

        Hand playerHand = new Hand();
        List<Card> handCards = new ArrayList<>(cards.subList(0, 5));
        playerHand.setCards(handCards);
        playerHand.setNumCards(handCards.size());
        handService.saveHand(playerHand);

        p.setHand(playerHand);
        playerService.savePlayer(p);
        assertNotNull(p.getId());
    }
    @Test
    void shouldUpdatePlayer() {
        int idToUpdate = 1;
        Player player = playerService.findPlayer(idToUpdate);
        player.setEnergy(2);
        playerService.updatePlayer(player);
        player = playerService.findPlayer(idToUpdate);
        assertEquals(2, player.getEnergy());
    }
    @Test
    void shouldDeletePlayer() {
        User user = this.userService.findUser(4);

        Player p = new Player();
        p.setEnergy(3);
        p.setUser(user);

        PackCard pc = new PackCard();
        List<Card> cards = simCreate25Cards(p);
        pc.setCards(cards);
        pc.setNumCards(cards.size());
        packCardService.savePackCard(pc);

        List<PackCard> packCards = new ArrayList<>();
        packCards.add(pc);
        p.setPackCards(packCards);

        Hand playerHand = new Hand();
        List<Card> handCards = new ArrayList<>(cards.subList(0, 5));
        playerHand.setCards(handCards);
        playerHand.setNumCards(handCards.size());
        handService.saveHand(playerHand);

        p.setHand(playerHand);
        playerService.savePlayer(p);
        playerService.deletePlayer(p);
    
        assertThrows(ResourceNotFoundException.class, () -> playerService.findPlayer(p.getId()));
    }
    @Test
    void shouldSaveUserPlayerbyUser() {
        User user = this.userService.findUser(4);

        Hand playerHand = new Hand();
        List<Card> cards = new ArrayList<>();
        playerHand.setCards(cards);
        playerHand.setNumCards(0);
        handService.saveHand(playerHand);

        Player p = playerService.saveUserPlayerbyUser(user, playerHand);
    
        assertNotNull(p.getId());
        assertNotNull(playerHand.getId());
    
    }
    @Test
    void shouldNotSaveUserPlayerbyUser() {
        User user = this.userService.findUser(4);

        Hand playerHand = new Hand();
        List<Card> cards = new ArrayList<>();
        playerHand.setCards(cards);
        playerHand.setNumCards(0);
        handService.saveHand(playerHand);

        Player p = playerService.saveUserPlayerbyUser(user, playerHand);
    
        assertNotNull(p.getId());
        assertNotNull(playerHand.getId());
    
    }
    @Test
    void shouldSavePlayerWithEmptyHand() {
        User user = this.userService.findUser(4);

        Hand emptyHand = new Hand();
        emptyHand.setCards(new ArrayList<>());
        emptyHand.setNumCards(0);

        handService.saveHand(emptyHand);

        Player p = playerService.saveUserPlayerbyUser(user, emptyHand);

        assertNotNull(p.getId());
        assertEquals(0, p.getHand().getNumCards());
    }
    @Test
    void shouldUpdatePlayerEnergy() {
        Player player = playerService.findPlayer(1);
        int initialEnergy = player.getEnergy();

        player.setEnergy(initialEnergy - 1);
        playerService.updatePlayer(player);

        player = playerService.findPlayer(1);
        assertEquals(initialEnergy - 1, player.getEnergy());
    }
   




    
    


    
}

    






    

