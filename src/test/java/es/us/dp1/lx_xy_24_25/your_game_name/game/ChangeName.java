package es.us.dp1.lx_xy_24_25.your_game_name.game;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
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
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvalidIndexOfTableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToJumpTeam;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToPlaceCard;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameService.Pair;
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
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
public class GameServiceTests {
    
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

    private Game simGame;

    private Player p;

    List<Card> simCreate25Cards(Player player) { //Función igual a cardService create25Cards que evita la simulación por mock del comportamiento de create25cards
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
        Player p2 = new Player();
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
        pc.setNumCards(cards.size());
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

        List<Player> players = List.of(p,p2,p3,p4);
        ChatMessage c = new ChatMessage();
        c.setMessageString("hello");;
        List<ChatMessage> chat = new ArrayList<>();
        chat.add(c);
        GameState state = GameState.IN_PROCESS;
        GameMode mode = GameMode.VERSUS;
        Player spectator = new Player();
        spectator.setId(2);
        List<Player> spectators = List.of(spectator);
        
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

        Card nodo3 = new Card();
        nodo3.setId(3);
        nodo3.setOutputs(List.of(2));
        nodo3.setPlayer(p3);
        nodo3.setRotation(0);
        nodo3.setType(TypeCard.INICIO);
        Cell cell3 = rows.get(5).getCells().get(4);
        cell3.setCard(nodo3);
        cell3.setIsFull(true);

        Card nodo4 = new Card();
        nodo4.setId(4);
        nodo4.setOutputs(List.of(2));
        nodo4.setPlayer(p4);
        nodo4.setRotation(0);
        nodo4.setType(TypeCard.INICIO);
        Cell cell4 = rows.get(4).getCells().get(3);
        cell4.setCard(nodo4);
        cell4.setIsFull(true);

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
    void verifyDefaultGameInitialization() {
        assertNotNull(simGame, "defaultGame debería estar inicializado en @BeforeEach");
        assertEquals(1, simGame.getId());
    }

    //Test de consulta de Game

    @Test
    void shouldFindAll(){
        //Simulo comportamiento de findAll
        List<Game> mockGames = List.of(simGame);
        when(gameRepository.findAll()).thenReturn(mockGames);
        //Llamo al repositorio
        List<Game> games = (List<Game>) gameService.findAll();
        //Compruebo el resultado
        assertEquals(1, games.size());
        assertEquals(1,games.get(0).getId());
    }


    @Test
    void shouldFindGame(){
        //Simulo el comportamiento de findById
        Mockito.when(gameRepository.findById(1)).thenReturn(Optional.of(simGame));
        // Llama al método del servicio
        Game game = gameService.findGame(1);
        // Verifica el resultado
        assertNotNull(game);
        assertEquals(1, game.getId());
    }

    @Test
    void shouldNotFindGame(){
        Mockito.when(gameRepository.findById(3423234)).thenReturn(Optional.empty());
        // Verifica que lanza la excepción
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGame(3423234));
    }

    @Test 
    void shouldFindGameByGameCode(){
        //Simula el comportamiento del repositorio
        when(gameRepository.findGameByGameCode("ABCDE")).thenReturn(Optional.of(simGame));
        //Llama al servicio
        Game gamebyGameCode = gameService.findGameByGameCode("ABCDE");
        //Comprueba el resultado
        assertNotNull(gamebyGameCode);
        assertEquals("ABCDE", gamebyGameCode.getGameCode());
    }

    @Test 
    void shouldNotFindGameByGameCode(){
        when(gameRepository.findGameByGameCode("AAAAA")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGameByGameCode("AAAAA"));
    }

    @Test
    void shouldFindJoinableGames(){
        //Simula el comportamiento del repositorio
        List<Game> joinableGames = List.of(simGame);
        List<GameState> validStates = List.of(GameState.IN_PROCESS,GameState.WAITING);
        when(gameRepository.findByGameStateIn(validStates)).thenReturn(joinableGames);
        //Llama al servicio
        List<Game> games = gameService.findJoinableGames();
        //Comprueba el resultado
        assertFalse(games.isEmpty());
        assertEquals(1, games.get(0).getId()); 
    }

    @Test
    void shouldNotFindJoinableGames(){
        //Simula el comportamiento del repositorio
        List<GameState> validStates = List.of(GameState.IN_PROCESS,GameState.WAITING);
        when(gameRepository.findByGameStateIn(validStates)).thenReturn(new ArrayList<>());
        //Llamo al servicio
        List<Game> joinableGames = gameService.findJoinableGames();
        //Compruebo el resultado
        assertEquals(0, joinableGames.size());
    }

    @Test
    void shouldSaveGame(){
        //Simulación del repositorio en el juego
        when(gameRepository.save(simGame)).thenReturn(simGame);
        //Llamada al servicio
        Game savedGame = gameService.saveGame(simGame);
        //Simulación del repositorio para obtener el juegoGuardado
        when(gameRepository.findAll()).thenReturn(List.of(savedGame));
        //Llamada al servicio
        List<Game> games = (List<Game>) gameService.findAll();
        assertNotNull(savedGame);
        assertEquals(1, games.size());
        assertEquals(1, savedGame.getId());
    }

    @Test 
    void shouldUpdateGame(){

        when(gameRepository.save(simGame)).thenReturn(simGame);
        Game gameToUpdated = gameService.saveGame(simGame);

        gameToUpdated.setDuration(30);
        when(gameRepository.save(simGame)).thenReturn(gameToUpdated);

        Game gameUpdated = gameService.updateGame(gameToUpdated);
        assertNotNull(gameUpdated);
        assertEquals(30,gameToUpdated.getDuration());      
    }

    @Test
    @Transactional
    void shouldDeleteGame() {
        // Lista simulada para representar los datos "persistidos"
        List<Game> mockDatabase = new ArrayList<>();
        mockDatabase.add(simGame);

        // Simular comportamiento de findAll()
        when(gameRepository.findAll()).thenAnswer(invocation -> new ArrayList<>(mockDatabase));

        // Simular comportamiento de findById()
        when(gameRepository.findById(anyInt())).thenAnswer(invocation -> {
            int id = invocation.getArgument(0);
            return mockDatabase.stream()
                    .filter(game -> game.getId() == id)
                    .findFirst();
        });

        // Simular comportamiento de delete()
        doAnswer(invocation -> {
            Game game = invocation.getArgument(0);
            mockDatabase.remove(game);
            return null;
        }).when(gameRepository).delete(any(Game.class));

        // Número de partidas inicial
        int count =((List<Game>) gameService.findAll()).size();
        System.out.println(count);

        // Borrar la partida
        gameService.deleteGame(simGame.getId());

        // Número de partidas final
        int finalCount = ((List<Game>) gameService.findAll()).size();
        System.out.println(finalCount);
        assertEquals(count - 1, finalCount);

        // Verificar que ya no se encuentra en el repositorio
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGame(simGame.getId()));
    }

    @Test
    void shouldGetGameChat(){
        //Simulo el comportamiento del repositorio
        when(gameRepository.findGameChat("ABCDE")).thenReturn(Optional.of(simGame.getChat()));
        //Llamo al servicio
        List<ChatMessage> chat = gameService.getGameChat("ABCDE");
        //Compruebo el resultado
        assertNotNull(chat);
        assertEquals(1, chat.size());
    }

    @Test
    void shouldNotGetGameChat(){
        when(gameRepository.findGameChat("AAAAA")).thenReturn(Optional.empty());//Para que salte la excepción orElseThrow debe devolver un Optional vacio
        assertThrows(ResourceNotFoundException.class, () -> gameService.getGameChat("AAAAA"));
    }

    @Test
    @Transactional
    void shouldSendMessage(){
        when(gameRepository.findGameChat("ABCDE")).thenReturn(Optional.of(simGame.getChat()));
        int iniChatSize = gameService.getGameChat("ABCDE").size();
        when(gameRepository.findGameByGameCode("ABCDE")).thenReturn(Optional.of(simGame));
        when(gameRepository.save(simGame)).thenReturn(simGame);
        ChatMessage cm = new ChatMessage();
        cm.setGameCode("ABCDE");
        cm.setMessageString("BYE");
        int finalChatSize = gameService.sendChatMessage(cm).size();
        assertNotEquals(iniChatSize, finalChatSize);
        assertEquals(2, finalChatSize);
    }

    @Test 
    @Transactional 
    void shouldTakeCard(){
        //Simulación de servicios llamados en takeCard
        when(this.handService.updateHand(simGame.getPlayers().get(0).getHand(), 1)).thenReturn(null);
        when(this.packCardService.updatePackCard(simGame.getPlayers().get(0).getPackCards().get(0), 1)).thenReturn(null);
        Player p  = simGame.getPlayers().get(0);
        PackCard packCard = p.getPackCards().stream().findFirst().get();
        int initialSizeCards = packCard.getNumCards();
        Pair<Player,Card> playerCard = gameService.takeACard(p);
        //Compruebo que el mazo tiene una carta menos
        int finalSizeCards = playerCard.getPlayer().getPackCards().get(0).getNumCards();;
        assertEquals(initialSizeCards - 1, finalSizeCards );
        //Compruebo que la carta está en la mano y que no está en el mazo 
        Card cardTaken = playerCard.getCard();
        assertFalse(playerCard.getPlayer().getPackCards().get(0).getCards().contains(cardTaken));
        assertTrue(playerCard.getPlayer().getHand().getCards().contains(cardTaken));
    }

    @Test 
    @Transactional
    void shouldInitialTurn(){
        //Comprobar que se ha creado un orden(la propiedad initialTurn no es null ni vacía)
        //Simulación de servicios y repos
        Game gameWithInitialTurn = gameService.initialTurn(simGame);
        assertNotNull(gameWithInitialTurn.getInitialTurn());
        assertTrue(gameWithInitialTurn.getInitialTurn().size()==4);
    }


    private Map<Integer,List<Card>> prepareTestDecideTurns1(){//Función auxiliar que prepara diferentes casos a comprobar para decidir el orden de los turnos.
        //Caso 1: Las inciativas de las ultimas cartas de los jugadores son distintas
        //Devuelve una map de entero lista de cartas
        List<Player> players1 = new ArrayList<>(); 
        Map<Integer,List<Card>> res = new HashMap<>();
        for(Integer i=0; i<=3;i++){
            Player p = simGame.getPlayers().get(i);
            List<Integer> playedCards = new ArrayList<>();
            switch (i) { //Hace una lista de cartas jugadas por cada jugador de la partida que simula la base de datos para cuando se ejecute el cardService.findCard()
                case 0:
                    Card c = Card.createByType(TypeCard.TYPE_1, p);
                    c.setId(i);
                    c.setPlayer(p);
                    List<Card> cardsPlayed = List.of(c);
                    res.put(i, cardsPlayed);
                    playedCards.add(i); 
                    break;
                case 1:
                    Card c1 = Card.createByType(TypeCard.TYPE_2_DER, p);
                    c1.setId(i);
                    c1.setPlayer(p);
                    List<Card> cardsPlayed1 = List.of(c1);
                    res.put(i, cardsPlayed1);
                    playedCards.add(i);
                    break;
                case 2:
                    Card c2 = Card.createByType(TypeCard.TYPE_3_DER, p);
                    c2.setId(i);
                    c2.setPlayer(p);
                    List<Card> cardsPlayed2 = List.of(c2);
                    res.put(i, cardsPlayed2);
                    playedCards.add(i);
                    break;
                case 3:
                    Card c3 = Card.createByType(TypeCard.TYPE_4, p);
                    c3.setId(i);
                    c3.setPlayer(p);
                    List<Card> cardsPlayed3 = List.of(c3);
                    res.put(i, cardsPlayed3);
                    playedCards.add(i);
                    break;
                default:
                    break;
            }
            p.setPlayedCards(playedCards);
            players1.add(p);
        }
        simGame.setPlayers(players1);
        return res;
    }

    @Test
    @Transactional
    void shouldDecideTurnsCase1(){ //Todas las ultimas cartas jugadas son diferentes
        //Datos necesarios para la simulación de la función: 
        Map<Integer,List<Card>> lastPlayedCardsPlayed = prepareTestDecideTurns1();
        when(cardService.findCard(anyInt())).thenAnswer(invocation -> {
            int id = invocation.getArgument(0); //id será cero ya que la lista de playedCards solo tiene un id de carta
            return lastPlayedCardsPlayed.get(id).get(0);//siempre van a tener una carta en este caso
        });
        when(playerService.findPlayer(anyInt())).thenAnswer( invocation -> {
            int id = invocation.getArgument(0); //id será uno ya que es el primer jugador en la lista despues de ordenar el turno
            return simGame.getPlayers().get(id);
        });
        when(playerService.updatePlayer(any(Player.class))).thenReturn(null);
        Game gameTurnsOrder = gameService.decideTurns(simGame, simGame.getPlayers());

        List<Integer> playersShouldBe = List.of(1,2,3,4);
        assertEquals(playersShouldBe,gameTurnsOrder.getOrderTurn());
        assertEquals(1, gameTurnsOrder.getTurn());
        System.out.println(gameTurnsOrder.getOrderTurn());
    }

 

    @Test
    @Transactional
    void shouldReturnCards(){ //Actualizar repo antes 

    }

    @Test 
    @Transactional
    void shouldChangeInitialHand(){

    }

    private Player createValidPlayer() {
        Player player = new Player();
        player.setId(10);
        player.setEnergy(3);
        player.setCardsPlayedThisTurn(1);
        player.setEnergyUsedThisRound(false);
        player.setUsedPowers(new ArrayList<>());
        player.setPossiblePositions(new ArrayList<>());
        player.setPossibleRotations(new ArrayList<>());

        List<Card> cards = simCreate25Cards(player);
        Hand hand = new Hand();
        hand.setCards(new ArrayList<>(cards.subList(0, 5)));
        hand.setId(10);
        hand.setNumCards(5);

        PackCard packCard = new PackCard();
        packCard.setId(10);
        packCard.setNumCards(20);
        packCard.setCards(new ArrayList<>(cards.subList(5, cards.size())));

        player.setHand(hand);
        player.setPackCards(List.of(packCard));
        return player;
    }

    @Test
    void shouldUseAccelerate() {
        //Creación jugador de prueba
        Player player = createValidPlayer();

        gameService.useAccelerate(player);
        assertTrue(player.getEnergyUsedThisRound());
        assertEquals(2, player.getEnergy());
        assertTrue(player.getUsedPowers().contains(PowerType.ACCELERATE));
        assertEquals(0, player.getCardsPlayedThisTurn());
        verify(playerService).updatePlayer(any(Player.class));
    }

    @Test
    void shouldUseBrake() {
        //Creación jugador de prueba
        Player player = createValidPlayer();

        gameService.useBrake(player);
        assertTrue(player.getEnergyUsedThisRound());
        assertEquals(2, player.getEnergy());
        assertTrue(player.getUsedPowers().contains(PowerType.BRAKE));
        assertEquals(2, player.getCardsPlayedThisTurn());
        verify(playerService).updatePlayer(any(Player.class));
    }

    @Test
    void shouldUseBackAway() {
        //Creación jugador de prueba
        Player player = createValidPlayer();
        //Creación nuevas posiciones posibles
        List<Map<String, Integer>> newPossiblePositions = new ArrayList<>();
        Map<String, Integer> mp1 = new HashMap<>();
        mp1.put("position", 4);
        mp1.put("rotation", 0);
        newPossiblePositions.add(mp1);
        Map<String, Integer> mp2 = new HashMap<>();
        mp2.put("position", 12);
        mp2.put("rotation", 1);
        newPossiblePositions.add(mp2);

        gameService.useBackAway(player, newPossiblePositions);
        assertTrue(player.getEnergyUsedThisRound());
        assertEquals(2, player.getEnergy());
        assertTrue(player.getUsedPowers().contains(PowerType.BACK_AWAY));
        assertEquals(2, player.getPossiblePositions().size());
        assertEquals(2, player.getPossibleRotations().size());
        assertTrue(player.getPossiblePositions().containsAll(List.of(4, 12)));
        assertTrue(player.getPossibleRotations().containsAll(List.of(0, 1)));
        verify(playerService).updatePlayer(any(Player.class));
    }

    @Test
    void shouldUseExtraGas() {
        //Creación jugador de prueba
        Player player = createValidPlayer();

        gameService.useExtraGas(player);
        assertTrue(player.getEnergyUsedThisRound());
        assertEquals(2, player.getEnergy());
        assertTrue(player.getUsedPowers().contains(PowerType.EXTRA_GAS));
        assertEquals(19, player.getPackCards().stream().findFirst().get().getNumCards());
        assertEquals(6, player.getHand().getNumCards());
        verify(playerService).updatePlayer(any(Player.class));
        verify(packCardService).updatePackCard(any(PackCard.class), anyInt());
        verify(handService).updateHand(any(Hand.class), anyInt());
    }

    @Test
    void shouldUseJumpTeam() {
        // Creación jugador de prueba
        Player player = createValidPlayer();
        // Creación nuevas posiciones posibles
        List<Map<String, Integer>> newPossiblePositions = new ArrayList<>();
        Map<String, Integer> mp1 = new HashMap<>();
        mp1.put("position", 4);
        mp1.put("rotation", 0);
        newPossiblePositions.add(mp1);
        Map<String, Integer> mp2 = new HashMap<>();
        mp2.put("position", 12);
        mp2.put("rotation", 1);
        newPossiblePositions.add(mp2);

        gameService.useJumpTeam(player, newPossiblePositions);
        assertTrue(player.getEnergyUsedThisRound());
        assertEquals(2, player.getEnergy());
        assertTrue(player.getUsedPowers().contains(PowerType.JUMP_TEAM));
        assertEquals(2, player.getPossiblePositions().size());
        assertEquals(2, player.getPossibleRotations().size());
        assertTrue(player.getPossiblePositions().containsAll(List.of(4, 12)));
        assertTrue(player.getPossibleRotations().containsAll(List.of(0, 1)));
        verify(playerService).updatePlayer(any(Player.class));
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
        simGame.setGameState(GameState.IN_PROCESS);

        //Jugador a perdido
        p.setState(PlayerState.LOST);
        assertThrows(AccessDeniedException.class, 
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 23, cardToPlace), 
            "You can't place this card");
        p.setState(PlayerState.PLAYING);
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
        Integer actualTurn = simGame.getTurn();
        Integer i = simGame.getOrderTurn().indexOf(actualTurn);
        i++;
        Integer newTurn = simGame.getOrderTurn().get(i);
        Player player = simGame.getPlayers().stream().filter(p -> p.getId() == newTurn).findFirst().get();

        when(gameRepository.findGameByGameCode(anyString())).thenReturn(Optional.of(simGame));
        when(playerService.findPlayer(anyInt())).thenReturn(player);
        assertThrows(AccessDeniedException.class, 
            () -> gameService.placeCard(simGame.getHost(), simGame.getGameCode(), 23, cardToPlace), 
            "You can't place this card, because it's not your turn");
        simGame.setTurn(actualTurn);
        verify(gameRepository).findGameByGameCode(anyString());
    }
}
