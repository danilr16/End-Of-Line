package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.Output;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@AutoConfigureTestDatabase
public class CellServiceTests {
    @Autowired
    private CellService cellService;

    @Autowired
    private CardRepository cardRepository;

    @Test
    void shouldFindAll(){
        List<Cell> cells =  (List<Cell>) this.cellService.findAll();
        assertTrue(!cells.isEmpty());
    }
    @Test
    void shouldThrowExceptionWhenCellNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> cellService.findCell(99999));
    }

    @Test
    void shouldThrowExceptionWhenCellToUpdateNotFound() {
        Cell updateData = new Cell();
        updateData.setIsFull(true);
        assertThrows(ResourceNotFoundException.class, () -> cellService.updateCell(updateData, 999));
    }

    @Test
    void shouldThrowExceptionWhenCellToDeleteNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> cellService.deleteCell(999));
    }
//Test para no actualizar la celda si la carta es diferente a la anterior 

//Test para que una vez que la celda esté ocupada no se pueda desocupar




    



    



    

    
}
