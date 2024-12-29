package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

@SpringBootTest
@AutoConfigureTestDatabase
public class RowSerciceTests {

    @Autowired
    private RowService rowService;

    @Autowired
    private CellService cellService;

    @Test
    void shouldFindAll() {
        Iterable<Row> rows = rowService.findAll();
        List<Row> aux = new ArrayList<>();
        rows.forEach(aux::add);
        assertTrue(!aux.isEmpty());
    }

    @Test 
    void shouldFindTheRightRow() {
        Integer id = 1;
        Row foundRow = rowService.findRow(id);
        assertTrue(foundRow.getId().equals(id));
    }

    @Test
    void shouldThrowExceptionIfRowNotFound() {
        Integer id = 999999;
        assertThrows(ResourceNotFoundException.class, () -> rowService.findRow(id));
    }

    @Test
    void shouldFindNewlyCretedRows() {
        Cell cell1 = new Cell();
        Cell cell2 = new Cell();
        Cell cell3 = new Cell();
        List<Cell> rowCells = List.of(cell1, cell2, cell3);

        Row newRow = new Row();
        newRow.setCells(rowCells);
        Row savedRow = rowService.saveRow(newRow);

        assertTrue(rowCells.equals(savedRow.getCells()));
        assertDoesNotThrow(() -> rowService.findRow(savedRow.getId()));
    }

    @Test
    void shouldNotFindDeletedRows() {
        Integer id = 1;
        rowService.deleteRow(id);

        assertThrows(ResourceNotFoundException.class, () -> rowService.findRow(id));
    }
    
}
