package es.us.dp1.lx_xy_24_25.your_game_name.hand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
@SpringBootTest
@AutoConfigureTestDatabase
public class HandServiceTests {

    @Autowired
    private HandService handService;

    @Test
    void shouldFindAll() {
        List<Hand> hands = (List<Hand>) handService.findAll();
        assertTrue(!hands.isEmpty());
    }

    @Test
    void shouldSaveHand(){
        Hand newHand = new Hand();
        newHand.setNumCards(6);
        Hand savedHand = this.handService.saveHand(newHand);
        assertNotNull(savedHand.getId());
        assertEquals(6, savedHand.getNumCards());
    }

    @Test
    void shouldUpdateHand(){
        Hand newHand = new Hand();
        newHand.setNumCards(3);
        Hand handToUpdate = this.handService.saveHand(newHand);

        handToUpdate.setNumCards(7);
        Hand updatedHand = this.handService.updateHand(handToUpdate, handToUpdate.getId());
        assertEquals(7, updatedHand.getNumCards());
    }

    @Test
    void shouldDeleteHand(){
        Hand newHand = new Hand();
        newHand.setNumCards(5);
        Hand handToDelete = this.handService.saveHand(newHand);
        int idToDelete = handToDelete.getId();
        this.handService.deleteHand(idToDelete);
        assertThrows(ResourceNotFoundException.class, () -> this.handService.findHand(idToDelete));
    }

    @Test
    void shouldNotFindById(){
        assertThrows(ResourceNotFoundException.class, () -> handService.findHand(99999));
    }

    @Test
    void shouldNotSaveHandWithNegativeCards(){
        Hand newHand = new Hand();
        newHand.setNumCards(-6);
        assertThrows(IllegalArgumentException.class, () -> this.handService.saveHand(newHand));
    }
}
