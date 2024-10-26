package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard.TypeTable;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
public class TableCardService {

    private TableCardRepository repository;
    private RowService rowService;
    private CellService cellService;
    private CardService cardService;

    @Autowired
    public TableCardService(TableCardRepository repository, RowService rowService, CellService cellService, CardService cardService){
        this.repository = repository;
        this.rowService = rowService;
        this.cellService = cellService;
        this.cardService = cardService;
    }

    @Transactional(readOnly = true)
    public Iterable<TableCard> findAll() {
        return repository.findAll();
    }

    @Transactional
    public TableCard saveTableCard(TableCard tableCard) throws DataAccessException {
        repository.save(tableCard);
        return tableCard;
    }

    @Transactional(readOnly = true)
	public TableCard findTableCard(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TableCard", "id", id));
	}	

    @Transactional
	public TableCard updateTableCard(@Valid TableCard tableCard, Integer idToUpdate) {
		TableCard toUpdate = findTableCard(idToUpdate);
		BeanUtils.copyProperties(tableCard, toUpdate, "id");
		repository.save(toUpdate);
		return toUpdate;
	}

    @Transactional
	public void deleteTableCard(Integer id) {
		TableCard toDelete = findTableCard(id);
		this.repository.delete(toDelete);
	}

    @Transactional
    public TableCard creaTableCard(Integer numJugadores, GameMode gameMode, Player player) {
        switch (gameMode) {
            case VERSUS:
                return createVersus(numJugadores, player);
            case PUZZLE_SINGLE:
                return createTableCard(5, numJugadores, player, 5, 3, 0);
            case PUZZLE_COOP:
                return createVersus(numJugadores, player);
            case TEAM_BATTLE:
                return createVersus(numJugadores, player);
            default:
                return null;
        }
    }

    private TableCard createVersus(Integer numJugadores, Player player) {//Tableros modo versus según número de jugadores
        switch (numJugadores) {
            case 2:
                return createTableCard(7, numJugadores, player, 7, 3, 0);
            case 3:
                return createTableCard(7, numJugadores, player, 6, 4, 0);
            case 4:
                return createTableCard(9, numJugadores, player, 4, 5, 0);
            case 5:
                return createTableCard(9, numJugadores, player, 6, 5, 0);
            case 6:
                return createTableCard(11, numJugadores, player, 5, 5, 0);
            case 7:
                return createTableCard(11, numJugadores, player, 7, 6, 2);
            case 8:
                return createTableCard(13, numJugadores, player, 5, 6, 0);
            default:
                return null;
        }
    }

    private TableCard createGenericTable(Integer n, Integer numJugadores) {//Crea tablero nxn
        TableCard tableCard = new TableCard();
        List<Row> rows = new ArrayList<>();
        for(int i=0;i<n;i++) {//Filas con celdas vacías
            Row row = new Row();
            List<Cell> cells = new ArrayList<>();
            for(int j=0;j<n;j++) {
                Cell cell = new Cell();
                cells.add(cell);
                cellService.saveCell(cell);
            }
            row.setCells(cells);
            rowService.saveRow(row);
            rows.add(row);
        }
        tableCard.setRows(rows);
        tableCard.setType(TypeTable.valueOf("JUGADORES_" + (numJugadores)));
        tableCard.setNumRow(n);
        tableCard.setNumColum(n);
        saveTableCard(tableCard);
        return tableCard;
    }

    private TableCard createTableCard(Integer n , Integer numJugadores, Player player, Integer f, Integer c, Integer rotation) {
        TableCard tableCard = createGenericTable(n, numJugadores);//TableroGenérico y luego creamos el nodo de inicio del host
        Cell cell = tableCard.rows.get(f-1).getCells().get(c-1);
        Card card = Card.createByType(TypeCard.INICIO, player);
        card.setRotation(rotation);
        cardService.saveCard(card);
        cell.setCard(card);
        cell.setIsFull(true);
        cellService.updateCell(cell, cell.getId());
        updateTableCard(tableCard, tableCard.getId());
        return tableCard;
    }
}
