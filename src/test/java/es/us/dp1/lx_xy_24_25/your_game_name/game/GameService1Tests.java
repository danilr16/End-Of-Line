package es.us.dp1.lx_xy_24_25.your_game_name.game;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ConflictException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToJumpTeam;
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
import es.us.dp1.lx_xy_24_25.your_game_name.team.Team;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
public class GameService1Tests {
    
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
    void shouldDecideTurnsCase2() {//Se prueba en este caso que todas las cartas de la ultima ronda tienen la misma iniciativa
        //Configuración necesaria para que el test ejecute sin errores
        Game testGame = simGame;

        //Configuro los juagdores
        Player testPlayer1 = createValidPlayer();
        Player testPlayer2 = createValidPlayer();
        Player testPlayer3 = createValidPlayer();
        testPlayer1.setId(1);
        testPlayer2.setId(2);
        testPlayer3.setId(3);

        //Configuro las cartas (para mayor sencillez tendrán igual id e iniciativa)
        Card c1 = Card.createByType(TypeCard.TYPE_1, testPlayer1);
        Card c2 = Card.createByType(TypeCard.TYPE_2_DER, testPlayer2);
        Card c3 = Card.createByType(TypeCard.TYPE_3_DER, testPlayer3);
        c1.setId(1);
        c1.setPlayer(testPlayer1);
        c1.setIniciative(1);
        c2.setId(2);
        c2.setPlayer(testPlayer2);
        c2.setIniciative(2);
        c3.setId(3);
        c3.setPlayer(testPlayer3);
        c3.setIniciative(3);

        //Configuro las cartas colocadas por cada jugador
        testPlayer1.setPlayedCards(List.of(3,1,2));
        testPlayer2.setPlayedCards(List.of(3,2,1));
        testPlayer3.setPlayedCards(List.of(3,2,3));

        //Se añaden los jugadores a la partida
        List<Player> testPlayerList = new ArrayList<>();
        testPlayerList.add(testPlayer1);
        testPlayerList.add(testPlayer2);
        testPlayerList.add(testPlayer3);

        testGame.setPlayers(testPlayerList);

        //Mockeo el comportanmiento de los servicios para que devuelvan los objetos creados para este test
        when(cardService.findCard(1)).thenReturn(c1);
        when(cardService.findCard(2)).thenReturn(c2);
        when(cardService.findCard(3)).thenReturn(c3);
        when(playerService.findPlayer(1)).thenReturn(testPlayer1);
        


        List<Integer> expectedOrderTurn = List.of(1,2,3); //Orden de turnos que debría devolver el método con la configuración establecida
        System.out.println("El orden de turnos debería ser este: " + gameService.decideTurns(testGame, testPlayerList).getOrderTurn());
        assertEquals(gameService.decideTurns(testGame, testPlayerList).getOrderTurn(), expectedOrderTurn);

    }

    @Test
    @Transactional
    void shouldDecideTurnsCase3() {//Se prueba en este caso que todas las cartas de todas las rondas jugadas 
        //tienen la misma inciativa, en este caso
        //se debería volver hasta el orden de turno de la primera ronda, que sería el resultado

        //Configuración necesaria para que el test ejecute sin errores
        Game testGame = simGame;

        //Configuro los juagdores
        Player testPlayer1 = createValidPlayer();
        Player testPlayer2 = createValidPlayer();
        Player testPlayer3 = createValidPlayer();
        testPlayer1.setId(1);
        testPlayer2.setId(2);
        testPlayer3.setId(3);

        //Configuro las cartas (para mayor sencillez tendrán igual id e iniciativa)
        Card c1 = Card.createByType(TypeCard.TYPE_1, testPlayer1);
        Card c2 = Card.createByType(TypeCard.TYPE_2_DER, testPlayer2);
        Card c3 = Card.createByType(TypeCard.INICIO, testPlayer3); //Carta de inicio
        c1.setId(1);
        c1.setPlayer(testPlayer1);
        c1.setIniciative(1);
        c2.setId(2);
        c2.setPlayer(testPlayer2);
        c2.setIniciative(2);
        c3.setId(3);
        c3.setPlayer(testPlayer3);
        c3.setIniciative(3);

        //Configuro las cartas colocadas por cada jugador, vemos en este caso que la primera carta colocada por cada jugador es la de inicio,
        //esto representa la situación en la que el método ha ido comparando las iniciativas de cada ronda (que en este test son iguales) hasta llegar a la ronda incial
        testPlayer1.setPlayedCards(List.of(3,2,1));
        testPlayer2.setPlayedCards(List.of(3,2,1));
        testPlayer3.setPlayedCards(List.of(3,2,1));

        //Se añaden los jugadores a la partida
        List<Player> testPlayerList = new ArrayList<>();
        testPlayerList.add(testPlayer1);
        testPlayerList.add(testPlayer2);
        testPlayerList.add(testPlayer3);

        testGame.setPlayers(testPlayerList);

        //Elijo un jugador al azar, que es lo que se hace en la primera ronda
        testGame.setInitialTurn(List.of(2));

        //Mockeo el comportanmiento de los servicios para que devuelvan los objetos creados para este test
        when(cardService.findCard(1)).thenReturn(c1);
        when(cardService.findCard(2)).thenReturn(c2);
        when(cardService.findCard(3)).thenReturn(c3);
        when(playerService.findPlayer(2)).thenReturn(testPlayer2);
        
        
        System.out.println("El orden de turnos debería ser este: " + gameService.decideTurns(testGame, testPlayerList).getOrderTurn());
        assertEquals(gameService.decideTurns(testGame, testPlayerList).getOrderTurn(), List.of(2));
        //Cuando se llega a la ronda inicial el método devuelve una lista que solo contiene al jugador que comienza el juego

    }
 

    @Test
    @Transactional
    void shouldReturnCards(){ //Actualizar repo antes 

        Player testPlayer = createValidPlayer(); //Creamos un jugador válido (con mano, cartas y demás)
        Hand playerHand = testPlayer.getHand();
        List<Card> playerCards = playerHand.getCards();
        PackCard playerPackCard = testPlayer.getPackCards().stream().findFirst().get();
        
        Integer expectedPackCardSize = playerPackCard.getNumCards() + playerCards.size();
        Integer expectedHandSize = playerHand.getNumCards() - playerCards.size(); //Estos son los tamaños que deberían tener hand y packCard tras llamar al método

        gameService.returnCards(testPlayer); // Se llama al método que se quiere probar


        assertTrue(playerCards.isEmpty());
        assertTrue(playerPackCard.getNumCards().equals(expectedPackCardSize));
        assertTrue(playerHand.getNumCards().equals(expectedHandSize));
        verify(handService).updateHand(any(Hand.class), any(Integer.class));
    }

    @Test 
    @Transactional
    void shouldChangeInitialHand(){

        GameService spyGs = Mockito.spy(gameService); //Se crea un spy de gameService, solo para esta prueba, esto permite hacer verificaciones sobre métodos del propio gameServie

        Player testPlayer = createValidPlayer();

        spyGs.changeInitialHand(testPlayer);

        verify(spyGs).returnCards(any(Player.class)); //Se comprueba que se devuelvan las cartas del jugador al mazo
        assertTrue(testPlayer.getHandChanged().equals(true)); //Aquí que se marque que el jugador ya ha gastado su posibilidad de cambiar de mano
        verify(playerService).updatePlayer(any(Player.class)); //Y aquí que se actualice al jugador llamando a playerService
    }

    @Test
    @Transactional
    void shouldManageTurnCase1() throws Exception {
        
        Game testGame = simGame; //Juego de prueba configurado previamente
        Player testPlayer = createValidPlayer(); //Jugador de prueba

        GameService spyGs = Mockito.spy(gameService); //De nuevo utiliza un spy ya que se llama al método manageTurnOfPlayer en la prueba

        testPlayer.setCardsPlayedThisTurn(2);
        doNothing().when(spyGs).nextTurn(any(Game.class), any(Player.class)); //Hago un stub porque esta prueba no es la que comprueba si nextTurn funciona bien
        
        spyGs.manageTurnOfPlayer(testGame, testPlayer);

        verify(spyGs).nextTurn(any(Game.class), any(Player.class));
    }

    @Test
    @Transactional
    void shouldManageTurnCase2() throws Exception {
        
        Game testGame = simGame;
        Player testPlayer = createValidPlayer();
        testPlayer.setTurnStarted(null);

        testGame.setNTurn(2); //Condición para que se lance la excepción

        assertThrows(ConflictException.class, () -> gameService.manageTurnOfPlayer(testGame, testPlayer));
    }

    @Test
    @Transactional
    void shouldManageTurnCase3() throws Exception {
        
        Game testGame = simGame;
        Player testPlayer = createValidPlayer();

        testGame.setNTurn(2);
        testPlayer.setTurnStarted(LocalDateTime.of(2024, 8, 23, 16, 28)); //Fecha aleatoria para que se cumpla la condicion que se quiere probar en este test

        gameService.manageTurnOfPlayer(testGame, testPlayer);

        assertTrue(testPlayer.getState() == PlayerState.LOST);
        verify(playerService).updatePlayer(any(Player.class));
    }

    @Test
    @Transactional
    void shouldManageTurnCase4() throws Exception {
        Game testGame = simGame; //Juego de prueba configurado previamente
        Player testPlayer = createValidPlayer(); //Jugador de prueba
        
        testGame.setNTurn(2);
        testPlayer.setTurnStarted(LocalDateTime.now());

        GameService spyGs = Mockito.spy(gameService); 
        doReturn(true).when(spyGs).cantContinuePlaying(any(Game.class), any(Player.class));
        
        spyGs.manageTurnOfPlayer(testGame, testPlayer);

        assertTrue(testPlayer.getState() == PlayerState.LOST);
        verify(spyGs).cantContinuePlaying(any(Game.class), any(Player.class));
        verify(playerService).updatePlayer(any(Player.class));
    }

    @Test
    @Transactional
    void shouldManageTurnCase5() throws Exception {
        Game testGame = simGame; //Juego de prueba configurado previamente
        Player testPlayer = createValidPlayer(); //Jugador de prueba

        GameService spyGs = Mockito.spy(gameService); 

        testGame.setNTurn(1);
        doNothing().when(spyGs).nextTurn(any(Game.class), any(Player.class));
        
        spyGs.manageTurnOfPlayer(testGame, testPlayer);

        verify(spyGs).nextTurn(any(Game.class), any(Player.class));
    }

    @Test
    void cantContinuePlayingCase1() throws Exception {

        Game testGame = simGame;
        Player testPlayer = createValidPlayer();

        testPlayer.getPackCards().stream().findFirst().get().setNumCards(0);
        testPlayer.getHand().setNumCards(0); //Hacemos que el jugador no tenga cartas, en esta situación perdería automáticamente

        assertTrue(gameService.cantContinuePlaying(testGame, testPlayer));
    }

    @Test
    void cantContinuePlayingCase2() throws Exception{

        Game testGame = simGame;
        Player testPlayer = createValidPlayer();

        testGame.setNTurn(4); //Suponemos que el juego está en el cuarto turno (ya se pueden usar poderes)
        testPlayer.setEnergyUsedThisRound(false); //Suponemos que el jugador no ha usado poderes esta ronda
        //Provocamos que el jugador no tenga posiciones disponibles para colocar cartas
        doReturn(new ArrayList<>()).when(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), nullable(Card.class), nullable(Player.class), any(Boolean.class));
        testPlayer.setEnergy(0); //En estas condiciones, el jugador NO podría seguir jugando al no tener más puntos de energía
    
        assertTrue(gameService.cantContinuePlaying(testGame, testPlayer));
    }

    @Test
    void cantContinuePlayingCase3() throws Exception {

        Game testGame = simGame;
        Player testPlayer = createValidPlayer();

        testGame.setNTurn(4); //Suponemos que el juego está en el cuarto turno (ya se pueden usar poderes)
        testPlayer.setEnergyUsedThisRound(true); //Suponemos que el jugador ha usado poderes esta ronda
        //Provocamos que el jugador no tenga posiciones disponibles para colocar cartas
        doReturn(new ArrayList<>()).when(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), nullable(Card.class), nullable(Player.class), any(Boolean.class));
        
        testPlayer.setUsedPowers(List.of(PowerType.ACCELERATE)); //Suponemos que ese es el ultimo poder que ha utilizado el jugador, en estas condiciones, habría perdido
        assertTrue(gameService.cantContinuePlaying(testGame, testPlayer));

        testPlayer.setUsedPowers(List.of(PowerType.BACK_AWAY)); //Suponemos ahora que el jugador ha usado el poder marcha atrás, por lo que aún podría seguir jugado
        testPlayer.setPossiblePositions(List.of(2,6));
        assertFalse(gameService.cantContinuePlaying(testGame, testPlayer));

    }

    @Test
    void cantContinuePlayingCase4() throws Exception {

        Game testGame = simGame;
        Player testPlayer = createValidPlayer();

        testGame.setNTurn(2); //Suponemos que estamos en la segunda ronda (aun no se pueden usar poderes)
        //Provocamos que el jugador no tenga posiciones disponibles para colocar cartas
        doReturn(new ArrayList<>()).when(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), nullable(Card.class), nullable(Player.class), any(Boolean.class));
    
        assertTrue(gameService.cantContinuePlaying(testGame, testPlayer));
    }

    @Test
    void cantContinuePlayingCase5() throws Exception {
        
        Game testGame = simGame;
        Player testPlayer = createValidPlayer();

        List<Map<String, Integer>> fakePossiblePositions = new ArrayList<>();
        fakePossiblePositions.add(Map.of("ab",1)); //Da igual que no sea una posición del juego, la cuestión es que possiblePositions no esté vacía
        //Provocamos que el jugador tenga alguna posición en la que pueda colocar cartas
        doReturn(fakePossiblePositions).when(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), nullable(Card.class), nullable(Player.class), any(Boolean.class));

        assertFalse(gameService.cantContinuePlaying(testGame, testPlayer));
    }

    @Test
    void cantUsePowersCase1() throws Exception {
        
        //La primera casuística (el jugador no tiene energía), ya se prueba en el test cantContinuePlayingCase2

        Game testGame = simGame;
        Player testPlayer = createValidPlayer();
        TableCard tc = testGame.getTable();
        Card lastPlaced = cardService.findCard(1); //Escojo una carta aleatoria

        testGame.setGameMode(GameMode.PUZZLE_SINGLE); //Hacemos que el modo de juego sea disitnto a team battle
        testPlayer.setPlayedCards(List.of(1,2,3)); //Suponemos que el jugador ha colocado suficientes cartas como para poder usar el poder marcha atrás

        List<Map<String, Integer>> fakePossiblePositions = new ArrayList<>();

        //Provocamos que el jugador NO tenga posiciones disponibles para colocar cartas (al usar marcha atras)
        doReturn(fakePossiblePositions).when(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), nullable(Card.class), nullable(Player.class), any(Boolean.class));
        assertTrue(gameService.cantUsePowers(testPlayer, testGame, tc, lastPlaced));

        fakePossiblePositions.add(Map.of("ab",1)); //Añadimos un elemento para que no esté vacía

        //Provocamos que el jugador tenga posiciones disponibles para colocar cartas (al usar marcha atras)
        doReturn(fakePossiblePositions).when(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), nullable(Card.class), nullable(Player.class), any(Boolean.class));
        assertFalse(gameService.cantUsePowers(testPlayer, testGame, tc, lastPlaced));
    }

    @Test
    void cantUsePowersCase2() throws Exception {

        Game testGame = simGame;
        Player testPlayer = createValidPlayer();
        TableCard tc = testGame.getTable();
        Card lastPlaced = cardService.findCard(1); //Escojo una carta aleatoria
        Team testTeam1 = new Team(); //Creamos dos equipos de prueba

        testGame.setGameMode(GameMode.TEAM_BATTLE);
        testPlayer.setPlayedCards(List.of(1)); //Forzamos que el jugador no pueda usar el poder marcha atrás, en este test comprobamos si funciona el salto
        List<Map<String, Integer>> fakePossiblePositions = new ArrayList<>();
        fakePossiblePositions.add(Map.of("ab",1)); //Añadimos un elemento para que no esté vacía
        testTeam1.setPlayer1(testPlayer); //Hacemos que el jugador de prueba esté en el equipo 1
        testGame.setTeams(List.of(testTeam1)); //Añadimos el equipo al juego

        //Hacemos que el jugador tenga alguna posición en la que pueda colocar cartas (al usar marcha atrás)
        doReturn(fakePossiblePositions).when(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), nullable(Card.class), nullable(Player.class), any(Boolean.class));
        assertFalse(gameService.cantUsePowers(testPlayer, testGame, tc, lastPlaced));

        //Forzamos ahora que se lance la excepcion UnfeasibleToJumpTeam para probar la otra casuística
        doThrow(UnfeasibleToJumpTeam.class).when(tableCardService).getPossiblePositionsForPlayer(any(TableCard.class), any(Player.class), nullable(Card.class), nullable(Player.class), any(Boolean.class));
        assertTrue(gameService.cantUsePowers(testPlayer, testGame, tc, lastPlaced));

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
        player.setPlayedCards(new ArrayList<>(List.of(1,2,3)));

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
}
