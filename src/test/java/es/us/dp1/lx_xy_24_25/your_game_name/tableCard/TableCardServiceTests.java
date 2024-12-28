package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.Output;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToJumpTeam;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard.TypeTable;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class TableCardServiceTests {

    @Mock
    private TableCardRepository repository;
    
    @InjectMocks
    private TableCardService mockedService;

    @Mock
    private RowService rowService;

    @Mock
    private CellService mockedCellService;

    @Mock
    private PlayerService mockedPlayerService;

    @Mock
    private CardService cardService;

    private TableCard tableCard;

    private Player player;

    private User user;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @Autowired
    private HandService handService;

    @Autowired
    private TableCardService tableCardService;

    @BeforeEach
    void setUp() {
        //Creamos un tablero vacío de 5x5
        tableCard = new TableCard();
        tableCard.setId(1);
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i<5; i++){
            Row row = new Row();
            List<Cell> cells = new ArrayList<>();
            for (int j = 0; j<5; j++){
                Cell cell = new Cell();
                cell.setCard(null);
                cell.setIsFull(false);
                cells.add(cell);
            }
            row.setCells(cells);
            rows.add(row);
        }
        tableCard.setRows(rows);
        tableCard.setNumColum(5);
        tableCard.setNumRow(5);

        //Creamos un jugador
        user = userService.findUser(4);
        Hand hand = handService.saveVoidHand();
        player = playerService.saveUserPlayerbyUser(user, hand);

        //Creamos una carta de inicio
        Card card = new Card();
        card.setPlayer(player);
        card.setType(TypeCard.INICIO);
        card.setOutputs(List.of(2));
        card.setOutput(Output.of(List.of(2), null));
        Cell cell = tableCard.getRows().get(4).getCells().get(2);
        cell.setCard(card);
        cell.setIsFull(true);
    }

    @Test
    void shouldFindAll() {
        List<TableCard> tableCards = List.of(tableCard);
        when(repository.findAll()).thenReturn(tableCards);
        List<TableCard> res = (List<TableCard>) mockedService.findAll();
        assertEquals(1, res.size());
        assertEquals(1, res.get(0).getId());
        verify(repository).findAll();
    }

    @Test
    void shouldSaveTable() {
        when(repository.save(tableCard)).thenReturn(tableCard);
        TableCard res = mockedService.saveTableCard(tableCard);
        assertEquals(1, res.getId());
        verify(repository).save(tableCard);
    }

    @Test
    void shouldFindById() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(tableCard));
        TableCard res = mockedService.findTableCard(1);
        assertEquals(1, res.getId());
        verify(repository).findById(1);
    }

    @Test
    void shouldNotFindById() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> mockedService.findTableCard(1));
        verify(repository).findById(1);
    }

    @Test
    void shouldUpdateTable() {
        tableCard.setType(TypeTable.JUGADORES_1);
        when(repository.save(tableCard)).thenReturn(tableCard);
        assertEquals(TypeTable.JUGADORES_1, mockedService.updateTableCard(tableCard).getType());
        verify(repository).save(tableCard);
    }

    @Test
    void shouldDeleteTable() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(tableCard));
        mockedService.deleteTableCard(1);
        verify(repository).findById(1);
        verify(repository).delete(tableCard);
    }

    @Test
    void shoulNotDeleteTable() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> mockedService.deleteTableCard(1));
    }

    @Test
    void shouldCreateSingleTable() throws UnfeasibleToJumpTeam {
        TableCard res = tableCardService.creaTableCard(List.of(player));
        assertEquals(5, res.getNumRow());
        assertEquals(5, res.getNumColum());
        assertEquals(TypeCard.INICIO, res.getRows().get(2).getCells().get(2).getCard().getType());
        assertEquals(List.of(8), player.getPossiblePositions());
        assertEquals(List.of(0), player.getPossibleRotations());
    }

    @Test
    void shouldCreateVersusTable() throws UnfeasibleToJumpTeam {
        Player other = playerService.saveUserPlayerbyUser(user, handService.saveVoidHand());
        TableCard res = tableCardService.creaTableCard(List.of(player, other));
        assertEquals(7, res.getNumRow());
        assertEquals(7, res.getNumColum());
        assertEquals(TypeCard.INICIO, res.getRows().get(3).getCells().get(2).getCard().getType());
        assertEquals(TypeCard.INICIO, res.getRows().get(3).getCells().get(4).getCard().getType());
        assertEquals(List.of(17), player.getPossiblePositions());
        assertEquals(List.of(0), player.getPossibleRotations());
    }

    @Test
    void shouldReturnPossiblePositions() throws UnfeasibleToJumpTeam {
        TableCard singleTable = tableCardService.creaTableCard(List.of(player));
        //Creamos carta y la ponemos en el tablero
        Card card = new Card();
        card.setId(1);
        card.setPlayer(player);
        card.setInput(0);
        card.setOutputs(List.of(1,2,3));
        card.setRotation(0);
        Cell cell = singleTable.getRows().get(1).getCells().get(2);
        cell.setCard(card);
        cell.setIsFull(true);
        List<Map<String, Integer>> res = tableCardService.getPossiblePositionsForPlayer(singleTable, player, card, null, false);
        assertFalse(res.isEmpty());
        assertTrue(res.size() == 3);
        assertTrue(res.stream().map(m -> m.get("position")).toList().containsAll(List.of(3,7,9)));
        assertTrue(res.stream().map(m -> m.get("rotation")).toList().containsAll(List.of(0,1,3)));
    }

    @Test
    void shouldReturnPossiblePositionsWithTeamJump() throws UnfeasibleToJumpTeam {
        Player team = playerService.saveUserPlayerbyUser(userService.findUser(5), handService.saveVoidHand());
        TableCard versusTable = tableCardService.creaTableCard(List.of(player, team));
        //Creamos las cartas y las ponemos en el tablero
        Card card1 = new Card();
        card1.setId(1);
        card1.setPlayer(player);
        card1.setInput(0);
        card1.setOutputs(List.of(1,2,3));
        card1.setRotation(0);
        Cell cell1 = versusTable.getRows().get(2).getCells().get(2);//Colocamos en (3,3)
        cell1.setCard(card1);
        cell1.setIsFull(true);
        Card card2 = new Card();
        card2.setId(2);
        card2.setPlayer(team);
        card2.setInput(0);
        card2.setOutputs(List.of(1,2,3));
        card2.setRotation(0);
        Cell cell2 = versusTable.getRows().get(2).getCells().get(4);//Colocamos en (3,5)
        cell2.setCard(card2);
        cell2.setIsFull(true);
        Card card3 = new Card();
        card3.setId(3);
        card3.setPlayer(team);
        card3.setInput(0);
        card3.setOutputs(List.of(1,2,3));
        card3.setRotation(3);
        Cell cell3 = versusTable.getRows().get(2).getCells().get(3);//Colocamos en (3,4)
        cell3.setCard(card3);
        cell3.setIsFull(true);

        List<Map<String, Integer>> res = tableCardService.getPossiblePositionsForPlayer(versusTable, team, card3, player, true);
        assertFalse(res.isEmpty());
        assertTrue(res.size() == 3);
        assertTrue(res.stream().map(m -> m.get("position")).toList().containsAll(List.of(11,16,25)));
        assertTrue(res.stream().map(m -> m.get("rotation")).toList().containsAll(List.of(0,2,3)));
    }

    @Test
    void shouldDecideIfATableIsFull() {
        assertFalse(tableCardService.tableCardFull(tableCard));
        for (Row row : tableCard.getRows()){
            for (Cell cell : row.getCells()){
                cell.setIsFull(true);
            }
        }
        assertTrue(tableCardService.tableCardFull(tableCard));
    }
}
