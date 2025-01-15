package es.us.dp1.lx_xy_24_25.your_game_name.packCard;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@SpringBootTest
@AutoConfigureTestDatabase
public class PackCardServiceTests {

    
    private PackCardService packCardService;
    private UserService userService;
    private PlayerService playerService;
    private HandService handService;

    @Autowired
    private PackCardServiceTests(PackCardService packCardService, UserService userService, PlayerService playerService, HandService handService){
        this.packCardService = packCardService;
        this.userService = userService;
        this.playerService = playerService;
        this.handService = handService;
    }

    @Test
    void shouldFindAll(){
        List<PackCard> packs = (List<PackCard>) this.packCardService.findAll();

        assertTrue(!packs.isEmpty());
    }
    
    @Test
    void shouldSavePackCard(){
        PackCard newPack = new PackCard();
        PackCard savedPack = this.packCardService.savePackCard(newPack);
        assertNotNull(savedPack.getId());
    }

    @Test
    void shouldFindPackCardById(){
        PackCard wantedPack = new PackCard();
        PackCard savedPack = this.packCardService.savePackCard(wantedPack);
        PackCard foundPack = this.packCardService.findPackCard(savedPack.getId());
        
        assertDoesNotThrow(() -> this.packCardService.findPackCard(savedPack.getId()));
        assertEquals(wantedPack.getId(), foundPack.getId());
    }

    @Test
    void shouldUpdatePackCard(){
        PackCard oldPack = new PackCard();
        PackCard packToUpdate = this.packCardService.savePackCard(oldPack);
        packToUpdate.setNumCards(15);
        PackCard updatedPack = this.packCardService.updatePackCard(packToUpdate, packToUpdate.getId());
        assertEquals(15, updatedPack.getNumCards());
    }

    @Test
    void shouldDeletepackCard(){
        PackCard newPack = new PackCard();
        PackCard packToDelete = this.packCardService.savePackCard(newPack);

        this.packCardService.deletePackCard(packToDelete.getId());
        assertThrows(ResourceNotFoundException.class, () -> this.packCardService.findPackCard(packToDelete.getId()));
    }

    @Test
    void shouldNotFindPackCardWithBadId(){
        Integer id = 99999;

        assertThrows(ResourceNotFoundException.class, () -> this.packCardService.findPackCard(id));
    }

    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        Hand hand1 = handService.saveVoidHand();
        Player player1 = playerService.saveUserPlayerbyUser(userService.findUser(4), hand1);
        players.add(player1);
        Hand hand2 = handService.saveVoidHand();
        Player player2 = playerService.saveUserPlayerbyUser(userService.findUser(5), hand2);
        players.add(player2);
        return players;
    }

    @Test
    void shouldCreatePackCards(){
        List<Player> players = createPlayers();
        Integer cont = ((List<PackCard>) packCardService.findAll()).size();
        List<PackCard> packCards = this.packCardService.creaPackCards(players);
        Integer newCont = ((List<PackCard>) packCardService.findAll()).size();
        assertEquals(2, packCards.size());
        assertEquals(cont + 2, newCont);
        assertTrue(packCards.stream().allMatch(p -> p.getNumCards() == 25));
        assertTrue(players.stream().allMatch(p -> !p.getPackCards().isEmpty()));
    }

    @Test
    void shouldNotCreatePackCards() {
        List<Player> players = new ArrayList<>();
        List<PackCard> packCards = this.packCardService.creaPackCards(players);
        assertTrue(packCards.isEmpty());
        assertTrue(players.stream().allMatch(p -> p.getPackCards().isEmpty()));
    }
}
