package es.us.dp1.lx_xy_24_25.your_game_name.game;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

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
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameService.Pair;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.CellService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
public class GameServicetests {
    
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

    List<Card> simCreate25Cards(Player player) { //Función igual a cardService create25Cards que evita la simulación por mock del comportamiento de create25cards
        List<Card> cards = new ArrayList<>();
        for(int i=1;i<=3;i++) {
            Card c1 = Card.createByType(TypeCard.TYPE_1, player);
            cards.add(c1);
            Card c2 = Card.createByType(TypeCard.TYPE_2_IZQ, player);
            cards.add(c2);
            Card c3 = Card.createByType(TypeCard.TYPE_2_DER, player);
            cards.add(c3);
            Card c4 = Card.createByType(TypeCard.TYPE_3_IZQ, player);
            cards.add(c4);
            Card c5 = Card.createByType(TypeCard.TYPE_3_DER, player);
            cards.add(c5);
            Card c6 = Card.createByType(TypeCard.TYPE_4, player);
            cards.add(c6);
            Card c7 = Card.createByType(TypeCard.TYPE_5, player);
            cards.add(c7);
            Card c8 = Card.createByType(TypeCard.TYPE_0, player);
            cards.add(c8);
        }
        Card c9 = Card.createByType(TypeCard.TYPE_1, player);
        cards.add(c9);
        return cards;
    }
    //Game simulado para comprobar el funcionamiento
    @BeforeEach
    void setUp(){
        simGame = new Game();
        simGame.setDuration(20);
        simGame.setGameCode("ABCDE");
        simGame.setId(1);
        simGame.setVersion(0);

        User host = new User();
        //Datos de jugadores
        Player p = new Player();
        p.setId(1);
        p.setState(PlayerState.PLAYING);
        Player p2 = new Player();
        p2.setId(2);
        p2.setState(PlayerState.PLAYING);
        Player p3 = new Player();
        p3.setId(3);
        p3.setState(PlayerState.PLAYING);
        Player p4 = new Player();
        p4.setId(4);
        p4.setState(PlayerState.PLAYING);
            //Crear packcard
        PackCard pc = new PackCard();
        List<Card> cards = simCreate25Cards(p);
        pc.setCards(cards);
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
        GameState state = GameState.WAITING;
        GameMode mode = GameMode.VERSUS;
        Player spectator = new Player();
        spectator.setId(2);
        List<Player> spectators = List.of(spectator);
        TableCard table = new TableCard();
       

        simGame.setHost(host);
        simGame.setChat(chat);
        simGame.setPlayers(players);
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
        when(gameRepository.findById(1)).thenReturn(Optional.of(simGame));
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
        when(gameRepository.findById(1)).thenReturn(Optional.of(simGame));
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


   




}
