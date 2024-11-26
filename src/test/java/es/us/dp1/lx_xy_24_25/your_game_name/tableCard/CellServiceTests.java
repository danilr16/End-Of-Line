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
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;



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
    //Test: la casilla no se actualiza si la carta es diferente a la anterior colocada
    @Test
    void shouldNotUpdateCellIfCardNotTheSame() {
        Cell cell = new Cell();
        cell.setId(1);
        cell.setIsFull(false);

        Card existingCard = new Card();
        existingCard.setId(1);
        existingCard.setType(Card.TypeCard.TYPE_1);
        existingCard.setRotation(0);
        existingCard.setPlayer(new Player());

        cell.setCard(existingCard);
        cellService.saveCell(cell);

        Card newCard = new Card();
        newCard.setId(2);
        newCard.setType(Card.TypeCard.TYPE_2_DER);
        newCard.setRotation(0);
        newCard.setPlayer(new Player());

        Cell updatedCell = new Cell();
        updatedCell.setId(1);
        updatedCell.setCard(newCard);
        updatedCell.setIsFull(true);

        assertThrows(IllegalArgumentException.class, () -> cellService.updateCell(updatedCell, 1));
    }


    //Test: si cell tiene una carta, IsFull debe ser true AL GUARDARLA 
    @Test
    void shouldSetIsFullTrueWhenSaveCellHasCard() {
        
        Cell cell = new Cell();
        cell.setId(1);
        cell.setIsFull(false); 

        Card card = new Card();
        card.setId(1);
        card.setType(Card.TypeCard.TYPE_1);
        card.setRotation(0);
        card.setPlayer(new Player());
        
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
        cell.setId(1);
        cell.setIsFull(false); 
        cellService.saveCell(cell);

        Card card = new Card();
        card.setId(1);
        card.setType(Card.TypeCard.TYPE_1);
        card.setRotation(0);
        card.setPlayer(new Player());

        cell.setCard(card);
        Cell updatedCell = cellService.updateCell(cell, cell.getId());

        assertTrue(updatedCell.getIsFull());
    }
    //Test: si se actualiza una celda para eliminar la carta, isFull debe ser false
    @Test
    void shouldSetIsFullFalseWhenUpdatingCellWithoutCard() {
        Cell cell = new Cell();
        cell.setId(1);

        Card card = new Card();
        card.setId(1);
        card.setType(Card.TypeCard.TYPE_1);
        card.setRotation(0);
        card.setPlayer(new Player());

        cell.setCard(card);
        cell.setIsFull(true);
        cellService.saveCell(cell);

        cell.setCard(null);
        Cell updatedCell = cellService.updateCell(cell, cell.getId());
        
        assertFalse(updatedCell.getIsFull());
    }






        



        



        

        
    }
