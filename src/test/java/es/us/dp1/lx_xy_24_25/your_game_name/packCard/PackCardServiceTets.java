package es.us.dp1.lx_xy_24_25.your_game_name.packCard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;

@SpringBootTest
@AutoConfigureTestDatabase
public class PackCardServiceTets {

    
    private PackCardService packCardService;


    @Autowired
    private PackCardServiceTets(PackCardService packCardService){
        this.packCardService = packCardService;
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
}
