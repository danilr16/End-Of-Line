package es.us.dp1.lx_xy_24_25.your_game_name.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ConflictException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvalidIndexOfTableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToJumpTeam;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToPlaceCard;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PowerType;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Cell;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.CellService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Row;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.team.Team;
import es.us.dp1.lx_xy_24_25.your_game_name.team.TeamService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
public class GameService2Tests {

    @Mock
    private GameRepository gameRepository;


    @InjectMocks //Clase que queremos probar inyectando las dependencias correspondientes
    private GameService gameService;

    //Dependencias del servicio
    @Mock
    private PackCardService packCardService; 

    @Mock
    private HandService handService;

    @Mock
    private PlayerService playerService;

    @Mock
    private CardService cardService;

    @Mock
    private TableCardService tableCardService;

    @Mock
    private CellService cellService;

    @Mock
    private TeamService teamService;

    private Game simGame;

    private Player p, p2;

    public static List<Card> simCreate25Cards(Player player) { //Función igual a cardService create25Cards que evita la simulación por mock del comportamiento de create25cards
        List<Card> cards = new ArrayList<>();
        for(int i=1;i<=3;i++) {
            Card c1 = Card.createByType(TypeCard.TYPE_1, player);
            c1.setId(1);
            cards.add(c1);
            Card c2 = Card.createByType(TypeCard.TYPE_2_IZQ, player);
            c2.setId(2);
            cards.add(c2);
            Card c3 = Card.createByType(TypeCard.TYPE_2_DER, player);
            c3.setId(3);
            cards.add(c3);
            Card c4 = Card.createByType(TypeCard.TYPE_3_IZQ, player);
            c4.setId(4);
            cards.add(c4);
            Card c5 = Card.createByType(TypeCard.TYPE_3_DER, player);
            c5.setId(5);
            cards.add(c5);
            Card c6 = Card.createByType(TypeCard.TYPE_4, player);
            c6.setId(6);
            cards.add(c6);
            Card c7 = Card.createByType(TypeCard.TYPE_5, player);
            c7.setId(7);
            cards.add(c7);
            Card c8 = Card.createByType(TypeCard.TYPE_0, player);
            c8.setId(8);
            cards.add(c8);
        }
        Card c9 = Card.createByType(TypeCard.TYPE_1, player);
        c9.setId(9);
        cards.add(c9);
        return cards;
    }
    //Game simulado para comprobar el funcionamiento
    @BeforeEach
    void setUp(){
        User host = new User();
        //Datos de jugadores
        p = new Player();
        p.setId(1);
        p.setState(PlayerState.PLAYING);
        p.setUser(host);
        p.setCardsPlayedThisTurn(0);
        p.setTurnStarted(LocalDateTime.now());
        User user2 = new User();
        user2.setId(2);
        p2 = new Player();
        p2.setId(2);
        p2.setState(PlayerState.PLAYING);
        p.setCardsPlayedThisTurn(0);
        p2.setUser(user2);
        User user3 = new User();
        user3.setId(3);
        Player p3 = new Player();
        p3.setId(3);
        p3.setUser(user3);
        p3.setState(PlayerState.PLAYING);
        User user4 = new User();
        user4.setId(4);
        Player p4 = new Player();
        p4.setId(4);
        p4.setState(PlayerState.PLAYING);
        p4.setUser(user4);
            //Crear packcard
        PackCard pc = new PackCard();
        List<Card> cards = simCreate25Cards(p);
        pc.setCards(new ArrayList<>(cards.subList(5, cards.size())));
        pc.setId(1);
        pc.setNumCards(20);
        List<PackCard> packCards = new ArrayList<>();
        packCards.add(pc);
        p.setPackCards(packCards);
            //Crear hand
        Hand playerHand = new Hand();
        playerHand.setId(1);
        List<Card> handCards = new ArrayList<>(cards.subList(0, 5));
        playerHand.setCards(handCards);
        playerHand.setNumCards(handCards.size());
        p.setHand(playerHand);
        PackCard pc2 = new PackCard();
        pc2.setNumCards(20);
        Hand h2 = new Hand();
        h2.setNumCards(5);
        p2.setHand(h2);
        p2.setPackCards(new ArrayList<>(List.of(pc2)));
        PackCard pc3 = new PackCard();
        pc3.setNumCards(20);
        Hand h3 = new Hand();
        h3.setNumCards(5);
        p3.setHand(h3);
        p3.setPackCards(new ArrayList<>(List.of(pc3)));
        PackCard pc4 = new PackCard();
        pc4.setNumCards(20);
        Hand h4 = new Hand();
        h4.setNumCards(5);
        p4.setHand(h4);
        p4.setPackCards(new ArrayList<>(List.of(pc4)));

        List<Player> players = new ArrayList<>(List.of(p,p2,p3,p4));
        ChatMessage c = new ChatMessage();
        c.setMessageString("hello");;
        List<ChatMessage> chat = new ArrayList<>();
        chat.add(c);
        GameState state = GameState.IN_PROCESS;
        GameMode mode = GameMode.VERSUS;
        Player spectator = new Player();
        spectator.setId(2);
        List<Player> spectators = new ArrayList<>(List.of(spectator));
        
        // Crear filas y columnas
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Row row = new Row();
            row.setId(i);
            List<Cell> cells = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                Cell cell = new Cell();
                cell.setId(j);
                cell.setCard(null);
                cell.setIsFull(false);
                cells.add(cell);
            }
            row.setCells(cells);
            rows.add(row);
        }

        //Crear nodos de inicio
        Card nodo1 = new Card();
        nodo1.setId(1);
        nodo1.setOutputs(List.of(2));
        nodo1.setPlayer(p);
        nodo1.setRotation(0);
        nodo1.setType(TypeCard.INICIO);
        Cell cell1 = rows.get(3).getCells().get(4);
        cell1.setCard(nodo1);
        cell1.setIsFull(true);
        p.setPlayedCards(new ArrayList<>(List.of(nodo1.getId())));
        p.setPossiblePositions(new ArrayList<>(List.of(23)));
        p.setPossibleRotations(new ArrayList<>(List.of(0)));

        Card nodo2 = new Card();
        nodo2.setId(2);
        nodo2.setOutputs(List.of(2));
        nodo2.setPlayer(p2);
        nodo2.setRotation(0);
        nodo2.setType(TypeCard.INICIO);
        Cell cell2 = rows.get(4).getCells().get(5);
        cell2.setCard(nodo2);
        cell2.setIsFull(true);
        p2.setPlayedCards(new ArrayList<>(List.of(nodo2.getId())));
        p2.setPossiblePositions(new ArrayList<>(List.of(43)));
        p2.setPossibleRotations(new ArrayList<>(List.of(1)));

        Card nodo3 = new Card();
        nodo3.setId(3);
        nodo3.setOutputs(List.of(2));
        nodo3.setPlayer(p3);
        nodo3.setRotation(0);
        nodo3.setType(TypeCard.INICIO);
        Cell cell3 = rows.get(5).getCells().get(4);
        cell3.setCard(nodo3);
        cell3.setIsFull(true);
        p3.setPlayedCards(new ArrayList<>(List.of(nodo3.getId())));
        p3.setPossiblePositions(new ArrayList<>(List.of(59)));
        p3.setPossibleRotations(new ArrayList<>(List.of(2)));

        Card nodo4 = new Card();
        nodo4.setId(4);
        nodo4.setOutputs(List.of(2));
        nodo4.setPlayer(p4);
        nodo4.setRotation(0);
        nodo4.setType(TypeCard.INICIO);
        Cell cell4 = rows.get(4).getCells().get(3);
        cell4.setCard(nodo4);
        cell4.setIsFull(true);
        p4.setPlayedCards(new ArrayList<>(List.of(nodo4.getId())));
        p4.setPossiblePositions(new ArrayList<>(List.of(39)));
        p4.setPossibleRotations(new ArrayList<>(List.of(3)));

        //Crear tablero
        TableCard table = new TableCard();
        table.setId(1);
        table.setNumRow(9);
        table.setNumColum(9);
        table.setRows(rows);
       
        //Crear juego
        simGame = new Game();
        simGame.setDuration(20);
        simGame.setGameCode("ABCDE");
        simGame.setId(1);
        simGame.setVersion(0);
        simGame.setTurn(1);
        simGame.setInitialTurn(new ArrayList<>(List.of(1,2,3,4)));
        simGame.setOrderTurn(new ArrayList<>(List.of(1,2,3,4)));
        simGame.setNTurn(1);
        simGame.setHost(host);
        simGame.setChat(chat);
        simGame.setPlayers(players);
        simGame.setNumPlayers(players.size());
        simGame.setGameState(state);
        simGame.setGameMode(mode);
        simGame.setSpectators(spectators);
        simGame.setTable(table);
    }

    @Test
    void shouldPlaceCard() throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard, UnfeasibleToJumpTeam {
        Integer cont = p.getHand().getNumCards();
        Card cardToPlace = p.getHand().getCards().stream().findFirst().get();
        List<Map<String,Integer>> newPossiblePositions = new ArrayList<>();
        Map<String,Integer> mp = new HashMap<>();
        mp.put("position", 14);
        mp.put("rotation", 0);
        newPossiblePositions.add(mp);
        
        when(gameRepository.findGameByGameCode(anyString())).thenReturn(Optional.of(simGame));
        when(playerService.findPlayer(anyInt())).thenReturn(p);
        when(tableCardService.fromListsToPossiblePositions(anyList(), anyList())).thenCallRealMethod();
        when(tableCardService.getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), 
            any(Card.class), any(), anyBoolean())).thenReturn(newPossiblePositions);
        
        gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 23, cardToPlace);

        Integer newCont = p.getHand().getNumCards();
        assertEquals(cont - 1, newCont);
        assertEquals(1, p.getCardsPlayedThisTurn());
        assertTrue(p.getPlayedCards().contains(cardToPlace.getId()));
        assertTrue(simGame.getTable().getRows().get(2).getCells().get(4).getCard().equals(cardToPlace));
        assertEquals(1, p.getPossiblePositions().size());
        assertEquals(1, p.getPossibleRotations().size());
        assertTrue(p.getPossiblePositions().contains(14));
        assertTrue(p.getPossibleRotations().contains(0));

        verify(gameRepository).findGameByGameCode(anyString());
        verify(playerService).findPlayer(anyInt());
        verify(cardService).updateCard(any(Card.class), anyInt());
        verify(handService).updateHand(any(Hand.class), anyInt());
        verify(cellService).updateCell(any(Cell.class), anyInt());
        verify(playerService).updatePlayer(any(Player.class));
        verify(tableCardService).fromListsToPossiblePositions(anyList(), anyList());
        verify(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), 
            any(Card.class), any(), anyBoolean());
    }

    @Test
    void shouldNotPlaceCardYouAreNotInTheGame() throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard, UnfeasibleToJumpTeam {
        Card cardToPlace = p.getHand().getCards().stream().findFirst().get();
        User user = new User();
        user.setId(19);
        user.setUsername("bad user");

        when(gameRepository.findGameByGameCode(anyString())).thenReturn(Optional.of(simGame));
        assertThrows(AccessDeniedException.class, 
            () -> gameService.placeCard(user, simGame.getGameCode(), 23, cardToPlace), 
            "You can't place this card, because you aren't in this game");
        
        verify(gameRepository).findGameByGameCode(anyString());
    }

    @Test
    void shouldNotPlaceCardYouCantPlaceNow() {
        Card cardToPlace = p.getHand().getCards().stream().findFirst().get();

        //Carta que no es del jugador
        Player badPlayer = new Player();
        Card badCard = new Card();
        badCard.setId(100);
        badCard.setPlayer(badPlayer);
        when(gameRepository.findGameByGameCode(anyString())).thenReturn(Optional.of(simGame));
        assertThrows(AccessDeniedException.class, 
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 23, badCard), 
            "You can't place this card");

        //Carta que no está en la mano
        Card cardNotInHand = p.getPackCards().get(0).getCards().stream().findFirst().get();
        assertThrows(AccessDeniedException.class, 
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 23, cardNotInHand), 
            "You can't place this card");

        //Juego no está en proceso
        simGame.setGameState(GameState.WAITING);
        assertThrows(AccessDeniedException.class, 
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 23, cardToPlace), 
            "You can't place this card");

        //Jugador a perdido
        p.setState(PlayerState.LOST);
        assertThrows(AccessDeniedException.class, 
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 23, cardToPlace), 
            "You can't place this card");
        verify(gameRepository, times(4)).findGameByGameCode(anyString());
    }

    @Test
    void shouldNotPlaceCardBadIndex() {
        Card cardToPlace = p.getHand().getCards().stream().findFirst().get();

        when(gameRepository.findGameByGameCode(anyString())).thenReturn(Optional.of(simGame));
        assertThrows(InvalidIndexOfTableCard.class, 
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), -2, cardToPlace));
        
        assertThrows(InvalidIndexOfTableCard.class,
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 1000, cardToPlace));
        verify(gameRepository, times(2)).findGameByGameCode(anyString());
    }

    @Test
    void shouldNotPlaceCardItsNotYourTurn() {
        Card cardToPlace = p.getHand().getCards().stream().findFirst().get();
        Integer i = simGame.getOrderTurn().indexOf(simGame.getTurn());
        i++;
        Integer newTurn = simGame.getOrderTurn().get(i);
        Player player = simGame.getPlayers().stream().filter(p -> p.getId() == newTurn).findFirst().get();

        when(gameRepository.findGameByGameCode(anyString())).thenReturn(Optional.of(simGame));
        when(playerService.findPlayer(anyInt())).thenReturn(player);
        assertThrows(AccessDeniedException.class, 
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 23, cardToPlace), 
            "You can't place this card, because it's not your turn");
        verify(gameRepository).findGameByGameCode(anyString());
    }

    private Game createANotStartedGame() {
        Hand h1 = new Hand();
        h1.setId(1);
        h1.setCards(new ArrayList<>());
        h1.setNumCards(0);
        Player p1 = new Player();
        p1.setId(1);
        p1.setHand(h1);
        PackCard pc1 = new PackCard();
        pc1.setId(1);
        List<Card> cards1 = simCreate25Cards(p1);
        pc1.setNumCards(25);
        pc1.setCards(cards1);
        p1.setPackCards(new ArrayList<>(List.of(pc1)));
        p1.setState(PlayerState.PLAYING);

        Hand h2 = new Hand();
        h2.setId(2);
        h2.setCards(new ArrayList<>());
        h2.setNumCards(0);
        Player p2 = new Player();
        p2.setId(2);
        p2.setHand(h2);
        PackCard pc2 = new PackCard();
        pc2.setId(2);
        List<Card> cards2 = simCreate25Cards(p2);
        pc2.setNumCards(25);
        pc2.setCards(cards2);
        p2.setPackCards(new ArrayList<>(List.of(pc2)));
        p2.setState(PlayerState.PLAYING);

        Game game = new Game();
        game.setPlayers(new ArrayList<>(List.of(p1, p2)));
        game.setGameState(GameState.IN_PROCESS);
        game.setInitialTurn(new ArrayList<>());
        game.setOrderTurn(new ArrayList<>());
        return game;
    }

    @Test
    void shouldDoTurn0() {
        Game game = createANotStartedGame();
        Player player = game.getPlayers().get(0);

        when(playerService.findPlayer(anyInt())).thenReturn(player);
        gameService.turn0(game, game.getPlayers());

        assertEquals(5, game.getPlayers().get(0).getHand().getNumCards());
        assertEquals(5, game.getPlayers().get(1).getHand().getNumCards());
        assertEquals(20, game.getPlayers().get(0).getPackCards().get(0).getNumCards());
        assertEquals(20, game.getPlayers().get(1).getPackCards().get(0).getNumCards());
        assertFalse(game.getInitialTurn().isEmpty());
        assertNotNull(game.getTurn());
        assertTrue(game.getOrderTurn().contains(1) && game.getOrderTurn().contains(2));
        assertEquals(1, game.getNTurn());
        assertNotNull(player.getTurnStarted());

        verify(playerService).findPlayer(anyInt());
        verify(packCardService, times(10)).updatePackCard(any(PackCard.class), anyInt());
        verify(handService, times(10)).updateHand(any(Hand.class), anyInt());
        verify(playerService).updatePlayer(any(Player.class));
        verify(gameRepository).save(any(Game.class));
    }

    private void placeCardInTable() {
        Card card = p.getHand().getCards().stream().findFirst().get();
        p.getHand().getCards().remove(card);
        p.getHand().setNumCards(p.getHand().getNumCards() - 1);

        Cell cell = simGame.getTable().getRows().get(2).getCells().get(4);
        cell.setIsFull(true);
        cell.setCard(card);

        p.getPlayedCards().add(card.getId());
        p.setCardsPlayedThisTurn(1);
        p.setPossiblePositions(new ArrayList<>(List.of(14)));
        p.setPossibleRotations(new ArrayList<>(List.of(0)));
    }

    @Test
    void shouldDoNextTurnWithoutNextRound() throws UnfeasibleToJumpTeam {
        placeCardInTable();
        List<Map<String,Integer>> newPossiblePositions = new ArrayList<>();
        Map<String,Integer> mp = new HashMap<>();
        mp.put("position", 1);
        mp.put("rotation", 0);
        newPossiblePositions.add(mp);

        when(playerService.findPlayer(anyInt())).thenReturn(p2);
        when(tableCardService.fromListsToPossiblePositions(any(), any())).thenCallRealMethod();
        when(tableCardService.getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), 
            any(), any(), anyBoolean())).thenReturn(newPossiblePositions);
        
        gameService.nextTurn(simGame, p);

        assertEquals(0, p.getCardsPlayedThisTurn());
        assertEquals(4, p.getHand().getNumCards());
        assertNull(p.getTurnStarted());
        assertNotNull(p2.getTurnStarted());
        assertEquals(1, simGame.getNTurn());
        assertEquals(2, simGame.getTurn());
        assertFalse(p2.getPossiblePositions().isEmpty());

        verify(playerService, times(5)).updatePlayer(any(Player.class));
        verify(playerService).findPlayer(anyInt());
        verify(gameRepository).save(any(Game.class));
        verify(tableCardService, times(3)).fromListsToPossiblePositions(any(), any());
        verify(tableCardService, times(3)).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), 
            any(), any(), anyBoolean());
    }

    @Test
    void shouldDoNextTurnWithNextRound() throws UnfeasibleToJumpTeam {
        placeCardInTable();
        List<Map<String,Integer>> newPossiblePositions = new ArrayList<>();
        Map<String,Integer> mp = new HashMap<>();
        mp.put("position", 1);
        mp.put("rotation", 0);
        newPossiblePositions.add(mp);
        simGame.setOrderTurn(List.of(4, 2, 3, 1));
        simGame.setTurn(1);
        Card card = simGame.getTable().getRows().get(3).getCells().get(4).getCard();

        when(tableCardService.fromListsToPossiblePositions(any(), any())).thenCallRealMethod();
        when(tableCardService.getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), 
            any(), any(), anyBoolean())).thenReturn(newPossiblePositions);
        when(cardService.findCard(anyInt())).thenReturn(card);
        when(playerService.findPlayer(anyInt())).thenAnswer( invocation -> {
            int id = invocation.getArgument(0);
            return simGame.getPlayers().stream().filter(p -> p.getId() == id).findFirst().get();
        });

        gameService.nextTurn(simGame, p);
        assertEquals(0, p.getCardsPlayedThisTurn());
        assertEquals(5, p.getHand().getNumCards());
        assertNotNull(p.getTurnStarted());
        assertEquals(2, simGame.getNTurn());
        assertEquals(1, simGame.getTurn());
        assertTrue(simGame.getOrderTurn().equals(List.of(1, 2, 3, 4)));

        verify(playerService, times(5)).updatePlayer(any(Player.class));
        verify(cardService).findCard(anyInt());
        verify(playerService, times(5)).findPlayer(anyInt());
        verify(gameRepository).save(any(Game.class));
        verify(tableCardService, times(3)).fromListsToPossiblePositions(any(), any());
        verify(tableCardService, times(3)).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), 
            any(), any(), anyBoolean());
    }

    @Test
    void shouldManageGameDifferentGameModes() throws ConflictException, UnfeasibleToJumpTeam {
        GameService spyService = Mockito.spy(gameService);
        doNothing().when(spyService).gameInProcessSingle(any(Game.class), any(User.class));
        doNothing().when(spyService).gameInProcess(any(Game.class), any(User.class));
        doNothing().when(spyService).gameInProcessCoop(any(Game.class), any(User.class));
        doNothing().when(spyService).gameInProcessTeam(any(Game.class), any(User.class));
        //VERSUS
        spyService.manageGame(simGame, p.getUser());
        verify(spyService).gameInProcess(any(Game.class), any(User.class));

        //PUZZLE_SINGLE
        simGame.setGameMode(GameMode.PUZZLE_SINGLE);
        spyService.manageGame(simGame, p.getUser());
        verify(spyService).gameInProcessSingle(any(Game.class), any(User.class));

        //PUZZLE_COOP
        simGame.setGameMode(GameMode.PUZZLE_COOP);
        spyService.manageGame(simGame, p.getUser());
        verify(spyService).gameInProcessCoop(any(Game.class), any(User.class));

        //TEAM_BATTLE
        simGame.setGameMode(GameMode.TEAM_BATTLE);
        spyService.manageGame(simGame, p.getUser());
        verify(spyService).gameInProcessTeam(any(Game.class), any(User.class));
    }

    @Test
    void shouldThrowConflictErrorManagingGame() throws ConflictException, UnfeasibleToJumpTeam {
        GameService spyService = Mockito.spy(gameService);
        doThrow(ConflictException.class).when(spyService).gameInProcess(any(Game.class), any(User.class));
        assertThrows(ConflictException.class, () -> spyService.manageGame(simGame, p.getUser()));
    }

    @Test
    void shouldManageUseOfJump() throws UnfeasibleToJumpTeam, InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
        GameService spyService = Mockito.spy(gameService);
        Team team= new Team();
        team.setPlayer1(p);
        team.setPlayer2(p2);
        simGame.setTeams(new ArrayList<>(List.of(team)));
        simGame.setGameMode(GameMode.TEAM_BATTLE);
        Card card = p.getHand().getCards().stream().findFirst().get();
        List<Map<String,Integer>> newPossiblePositions = new ArrayList<>();
        Map<String,Integer> mp = new HashMap<>();
        mp.put("position", 2);
        mp.put("rotation", 0);
        newPossiblePositions.add(mp);

        when(cardService.getLastPlaced(any(Player.class))).thenReturn(card);
        when(tableCardService.getPossiblePositionsForPlayer(any(TableCard.class), 
            any(Player.class), any(Card.class), any(Player.class), anyBoolean())).thenReturn(newPossiblePositions);
        doNothing().when(spyService).useJumpTeam(any(Player.class), anyList());

        ResponseEntity<MessageResponse> response = spyService.manageUseOfEnergy(PowerType.JUMP_TEAM, p, simGame);
        assertEquals("You have used JUMP_TEAM successfully", response.getBody().getMessage());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        verify(cardService).getLastPlaced(any(Player.class));
        verify(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), 
            any(Player.class), any(Card.class), any(Player.class), anyBoolean());
        verify(spyService).useJumpTeam(any(Player.class), anyList());
    }

    @Test
    void shouldManageUseOfJumpCantJump() throws UnfeasibleToJumpTeam, InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
        GameService spyService = Mockito.spy(gameService);
        Team team= new Team();
        team.setPlayer1(p);
        team.setPlayer2(p2);
        simGame.setTeams(new ArrayList<>(List.of(team)));
        simGame.setGameMode(GameMode.TEAM_BATTLE);
        Card card = p.getHand().getCards().stream().findFirst().get();
        List<Map<String,Integer>> newPossiblePositions = new ArrayList<>();

        when(cardService.getLastPlaced(any(Player.class))).thenReturn(card);
        when(tableCardService.getPossiblePositionsForPlayer(any(TableCard.class), 
            any(Player.class), any(Card.class), any(Player.class), anyBoolean())).thenReturn(newPossiblePositions);

        ResponseEntity<MessageResponse> response = spyService.manageUseOfEnergy(PowerType.JUMP_TEAM, p, simGame);
        assertEquals("You can not jump your team", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(cardService).getLastPlaced(any(Player.class));
        verify(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), 
            any(Player.class), any(Card.class), any(Player.class), anyBoolean());
    }

    @Test
    void shouldManageUseOfJumpNotInTeamBattle() throws UnfeasibleToJumpTeam, InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
        GameService spyService = Mockito.spy(gameService);

        ResponseEntity<MessageResponse> response = spyService.manageUseOfEnergy(PowerType.JUMP_TEAM, p, simGame);
        assertEquals("You can only use this power in Team Battle", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldManageUseOfBackAway() throws UnfeasibleToJumpTeam, InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
        GameService spyService = Mockito.spy(gameService);
        Card card1 = p.getHand().getCards().get(0);
        Card card2 = p.getHand().getCards().get(1);
        p.getPlayedCards().add(card1.getId());
        p.getPlayedCards().add(card2.getId());
        List<Map<String,Integer>> newPossiblePositions = new ArrayList<>();
        Map<String,Integer> mp = new HashMap<>();
        mp.put("position", 2);
        mp.put("rotation", 0);
        newPossiblePositions.add(mp);

        when(cardService.findCard(anyInt())).thenReturn(card1);
        when(tableCardService.getPossiblePositionsForPlayer(any(TableCard.class), 
            any(Player.class), any(Card.class), any(), anyBoolean())).thenReturn(newPossiblePositions);
        doNothing().when(spyService).useBackAway(any(Player.class), anyList());

        ResponseEntity<MessageResponse> response = spyService.manageUseOfEnergy(PowerType.BACK_AWAY, p, simGame);
        assertEquals("You have used BACK_AWAY successfully", response.getBody().getMessage());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        verify(cardService).findCard(anyInt());
        verify(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), 
            any(Player.class), any(Card.class), any(), anyBoolean());
        verify(spyService).useBackAway(any(Player.class), anyList());
    }

    @Test
    void shouldManageUseOfBackAwayNotPossiblePositions() throws UnfeasibleToJumpTeam, InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
        GameService spyService = Mockito.spy(gameService);
        Card card1 = p.getHand().getCards().get(0);
        Card card2 = p.getHand().getCards().get(1);
        p.getPlayedCards().add(card1.getId());
        p.getPlayedCards().add(card2.getId());
        List<Map<String,Integer>> newPossiblePositions = new ArrayList<>();

        when(cardService.findCard(anyInt())).thenReturn(card1);
        when(tableCardService.getPossiblePositionsForPlayer(any(TableCard.class), 
            any(Player.class), any(Card.class), any(), anyBoolean())).thenReturn(newPossiblePositions);

        ResponseEntity<MessageResponse> response = spyService.manageUseOfEnergy(PowerType.BACK_AWAY, p, simGame);
        assertEquals("You can not use back away right now", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(cardService).findCard(anyInt());
        verify(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), 
            any(Player.class), any(Card.class), any(), anyBoolean());
    }

    @Test
    void shouldManageUseOfBackAwayNotEnoughCards() throws UnfeasibleToJumpTeam, InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
        GameService spyService = Mockito.spy(gameService);

        ResponseEntity<MessageResponse> response = spyService.manageUseOfEnergy(PowerType.BACK_AWAY, p, simGame);
        assertEquals("You can not use back away right now", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldManageAnotherEnergies() throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard, UnfeasibleToJumpTeam {
        GameService spyService = Mockito.spy(gameService);
        doNothing().when(spyService).useAccelerate(any(Player.class));
        doNothing().when(spyService).useBrake(any(Player.class));
        doNothing().when(spyService).useExtraGas(any(Player.class));

        ResponseEntity<MessageResponse> response1 = spyService.manageUseOfEnergy(PowerType.ACCELERATE, p, simGame);
        assertEquals("You have used ACCELERATE successfully", response1.getBody().getMessage());
        assertEquals(HttpStatus.ACCEPTED, response1.getStatusCode());

        ResponseEntity<MessageResponse> response2 = spyService.manageUseOfEnergy(PowerType.BRAKE, p, simGame);
        assertEquals("You have used BRAKE successfully", response2.getBody().getMessage());
        assertEquals(HttpStatus.ACCEPTED, response2.getStatusCode());

        ResponseEntity<MessageResponse> response3 = spyService.manageUseOfEnergy(PowerType.EXTRA_GAS, p, simGame);
        assertEquals("You have used EXTRA_GAS successfully", response3.getBody().getMessage());
        assertEquals(HttpStatus.ACCEPTED, response3.getStatusCode());

        PackCard packCard = p.getPackCards().stream().findFirst().get();
        packCard.setNumCards(0);
        ResponseEntity<MessageResponse> response4 = spyService.manageUseOfEnergy(PowerType.EXTRA_GAS, p, simGame);
        assertEquals("You can not take a card now, because your deck is empty", response4.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    void shouldCheckTeamBatlle3Players() {
        simGame.getPlayers().remove(simGame.getPlayers().size()-1);
        Team team1= new Team();
        team1.setPlayer1(p);
        team1.setPlayer2(p2);
        Team team2 = new Team();
        Player p3 = simGame.getPlayers().get(2);
        team2.setPlayer1(p3);
        simGame.setTeams(new ArrayList<>(List.of(team1, team2)));
        simGame.setGameMode(GameMode.TEAM_BATTLE);

        gameService.checkTeamBattle(simGame);
        assertEquals(GameMode.VERSUS, simGame.getGameMode());
        assertTrue(simGame.getTeams().isEmpty());

        verify(teamService, times(2)).deleteTeam(any(Team.class));
    }

    @Test
    void shouldCheckTeamBatlle5Players() {
        Player player = new Player();
        player.setId(5);
        Team team1= new Team();
        team1.setPlayer1(p);
        team1.setPlayer2(p2);
        Team team2 = new Team();
        Player p3 = simGame.getPlayers().get(2);
        Player p4 = simGame.getPlayers().get(3);
        team2.setPlayer1(p3);
        team2.setPlayer2(p4);
        Team team3 = new Team();
        team3.setPlayer1(player);
        simGame.setTeams(new ArrayList<>(List.of(team1, team2, team3)));
        simGame.setGameMode(GameMode.TEAM_BATTLE);
        simGame.getPlayers().add(player);

        gameService.checkTeamBattle(simGame);
        assertEquals(GameMode.TEAM_BATTLE, simGame.getGameMode());
        assertEquals(2, simGame.getTeams().size());
        assertEquals(PlayerState.SPECTATING, player.getState());
        assertEquals(4, simGame.getPlayers().size());
        assertEquals(2, simGame.getSpectators().size());

        verify(teamService).deleteTeam(any(Team.class));
        verify(playerService).updatePlayer(any(Player.class));
    }
}