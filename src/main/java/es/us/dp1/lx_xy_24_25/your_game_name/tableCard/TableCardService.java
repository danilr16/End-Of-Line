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
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard.TypeTable;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard.nodeCoordinates;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TableCardService {

    private TableCardRepository repository;
    private RowService rowService;
    private CellService cellService;
    private CardService cardService;
    private PlayerService playerService;

    @Autowired
    public TableCardService(TableCardRepository repository, RowService rowService, 
        CellService cellService, CardService cardService, PlayerService playerService){
        this.repository = repository;
        this.rowService = rowService;
        this.cellService = cellService;
        this.cardService = cardService;
        this.playerService = playerService;
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
    public TableCard creaTableCard(Integer numJugadores, GameMode gameMode, List<Player> players) {
        TableCard tableCard = null;
        Map<Integer, List<nodeCoordinates>> mp = TableCard.homeNodes();//Coordenadas de los nodos de inicio según el número de jugadores
        Map<Integer, Integer> aux = Map.of(1, 5, 2, 7, 3, 7, 4, 9, 5, 9, 6, 11, 7, 11, 8, 13);//Tamaño del tablero según el número de jugadores
        List<nodeCoordinates> ls = mp.get(numJugadores);
        tableCard = createGenericTable(aux.get(numJugadores), numJugadores);
        for (int i = 0; i < numJugadores; i++) {
            createHomeNode(tableCard, players.get(i), ls.get(i).f(), ls.get(i).c(), ls.get(i).rotation());
        }
        updateTableCard(tableCard, tableCard.getId());
        return tableCard;
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

    private void createHomeNode(TableCard tableCard, Player player, Integer f, Integer c, Integer rotation) {//Actualiza la celda y crea ahí el nodo de inicio
        Cell cell = tableCard.rows.get(f-1).getCells().get(c-1);
        Card card = Card.createByType(TypeCard.INICIO, player);
        card.setRotation(rotation);
        cardService.saveCard(card);
        player.getPlayedCards().add(card.getId());
        playerService.updatePlayer(player, player.getId());
        cell.setCard(card);
        cell.setIsFull(true);
        cellService.updateCell(cell, cell.getId());
    }


    @Transactional
    public Cell getCellAt(TableCard tableCard, Integer f, Integer c) {
        return tableCard.getRows().get(f-1).getCells().get(c-1);
    }

    @Transactional
    public Map<String, Integer> getPositionOfCard(TableCard tableCard, Card card) {
        for (int row = 0; row < tableCard.getRows().size(); row++) {
            List<Cell> cells = tableCard.getRows().get(row).getCells();
            for (int col = 0; col < cells.size(); col++) {
                Cell cell = cells.get(col);
                if (cell.getCard() != null && cell.getCard().getId().equals(card.getId())) {
                    Map<String, Integer> position = new HashMap<>();
                    position.put("x", col + 1); 
                    position.put("y", row + 1); 
                    return position;
                }
            }
        }
        return null; 
    }

    public Integer getCellIndexFromPosition(TableCard tableCard, Integer x, Integer y) {
        return ((y-1)*tableCard.getNumColum())+x;
    }

    @Transactional
    public List<Map<String, Integer>> getPossiblePositionsForPlayer(TableCard tableCard, Player player, Card placedCard) {
        Map<String, Integer> lastPlacedPos = getPositionOfCard(tableCard, placedCard);

        List<Map<String, Integer>> rotationToVector = RotationVectors.createRotationToVector();

        List<Map<String,Integer>> possiblePositions = new ArrayList<>();

        for (Integer outputIndex : placedCard.getOutputs()) {
            Map<String, Integer> vector = rotationToVector.get(outputIndex);
    
            int newX = Math.floorMod(lastPlacedPos.get("x") + vector.get("x")-1,tableCard.getNumRow())+1;
            int newY = Math.floorMod(lastPlacedPos.get("y") + vector.get("y")-1,tableCard.getNumColum())+1;
    
            if (getCellAt(tableCard, newY, newX).getIsFull() == false) {
                Map<String, Integer> newPosition = new HashMap<>();
                newPosition.put("position", getCellIndexFromPosition(tableCard, newX, newY));
                newPosition.put("rotation",(outputIndex+2)%4);
                possiblePositions.add(newPosition);
            }
        }
    
        return possiblePositions;
    }
}
