package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;


import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertThrows;


import java.util.List;


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
public class CellServiceTests {
    @Autowired
    private CellService cellService;

    @Autowired
    private CardService cardService;

    @Autowired
    private PlayerService playerService;

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
    //Test: la casilla no se actualiza si la carta es diferente a la anterior colocada
    @Test
    void shouldNotUpdateCellIfCardNotTheSame() {
        Cell cell = new Cell();
        cell.setIsFull(false);

        Player player = playerService.findPlayer(1);
        Card existingCard = Card.createByType(TypeCard.TYPE_1, player);
        cardService.saveCard(existingCard);

        cell.setCard(existingCard);
        cellService.saveCell(cell);

        Card newCard = Card.createByType(TypeCard.TYPE_1, player);
        cardService.saveCard(newCard);

        cell.setCard(newCard);

        assertThrows(IllegalArgumentException.class, () -> cellService.updateCell(cell, cell.getId()));
    }


    //Test: si cell tiene una carta, IsFull debe ser true AL GUARDARLA 
    @Test
    void shouldSetIsFullTrueWhenSaveCellHasCard() {
        
        Cell cell = new Cell();
        cell.setId(1);
        cell.setIsFull(false); 

        Player player = playerService.findPlayer(1);
        Card card = Card.createByType(TypeCard.TYPE_1, player);
        cardService.saveCard(card);
        
        cell.setCard(card);

        // Guardar la celda
        Cell savedCell = cellService.saveCell(cell);

        // Verificar que isFull se haya actualizado a true
        assertTrue(savedCell.getIsFull());
    }

    //Test: si cell tiene una carta, IsFull debe ser true AL ACTUALIZARLA 
    @Test
    void shouldSetIsFullTrueWhenUpdatingCellWithCard() {
        Cell cell = new Cell();
        cell.setIsFull(false); 
        cellService.saveCell(cell);

        Player player = playerService.findPlayer(1);
        Card card = Card.createByType(TypeCard.TYPE_1, player);
        cardService.saveCard(card);

        cell.setCard(card);
        Cell updatedCell = cellService.updateCell(cell, cell.getId());

        assertTrue(updatedCell.getIsFull());
    }
    //Test: si se actualiza una celda para eliminar la carta, isFull debe ser false
    @Test
    void shouldSetIsFullFalseWhenUpdatingCellWithoutCard() {
        Cell cell = new Cell();

        Player player = playerService.findPlayer(1);
        Card card = Card.createByType(TypeCard.TYPE_1, player);
        cardService.saveCard(card);

        cell.setCard(card);
        cell.setIsFull(true);
        cellService.saveCell(cell);

        cell.setCard(null);
        Cell updatedCell = cellService.updateCell(cell, cell.getId());
        
        assertFalse(updatedCell.getIsFull());
    }






        



        



        

        
    }
