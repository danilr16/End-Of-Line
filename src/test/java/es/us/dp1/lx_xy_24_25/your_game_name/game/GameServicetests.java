package es.us.dp1.lx_xy_24_25.your_game_name.game;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.GeoModule;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
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

    //Game simulado para comprobar el funcionamiento
    @BeforeEach
    public void setUp(){
        simGame = new Game();
        simGame.setDuration(20);
        simGame.setGameCode("ABCDE");
        simGame.setId(1);

        User host = new User();
        Player p = new Player();
        p.setId(1);
        List<Player> players = List.of(p);
        ChatMessage c = new ChatMessage();
        c.setMessageString("hello");;
        List<ChatMessage> chat = List.of(c);
        GameState state = GameState.WAITING;
        GameMode mode = GameMode.VERSUS;
        Player p2 = new Player();
        p2.setId(2);
        List<Player> spectators = List.of(p2);
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
    public void verifyDefaultGameInitialization() {
        assertNotNull(simGame, "defaultGame debería estar inicializado en @BeforeEach");
        assertEquals(1, simGame.getId());
    }

    //Test de consulta de Game

    @Test
    public void shouldFindAll(){
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
    public void shouldFindGame(){
        //Simulo el comportamiento de findById
        Mockito.when(gameRepository.findById(1)).thenReturn(Optional.of(simGame));
        // Llama al método del servicio
        Game game = gameService.findGame(1);
        // Verifica el resultado
        assertNotNull(game);
        assertEquals(1, game.getId());
    }

    @Test
    public void shouldNotFindGame(){
        Mockito.when(gameRepository.findById(3423234)).thenReturn(Optional.empty());
        // Verifica que lanza la excepción
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGame(3423234));
    }

    @Test 
    public void shouldFindGameByGameCode(){
        //Simula el comportamiento del repositorio
        when(gameRepository.findGameByGameCode("ABCDE")).thenReturn(Optional.of(simGame));
        //Llama al servicio
        Game gamebyGameCode = gameService.findGameByGameCode("ABCDE");
        //Comprueba el resultado
        assertNotNull(gamebyGameCode);
        assertEquals("ABCDE", gamebyGameCode.getGameCode());
    }

    @Test 
    public void shouldNotFindGameByGameCode(){
        when(gameRepository.findGameByGameCode("AAAAA")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGameByGameCode("AAAAA"));
    }

    @Test
    public void shouldFindJoinableGames(){
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
    public void shouldSaveGame(){
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
    public void shouldUpdateGame(){

        when(gameRepository.save(simGame)).thenReturn(simGame);
        Game gameToUpdated = gameService.saveGame(simGame);

        gameToUpdated.setDuration(30);
        when(gameRepository.findById(1)).thenReturn(Optional.of(simGame));
        when(gameRepository.save(simGame)).thenReturn(gameToUpdated);

        Game gameUpdated = gameService.updateGame(gameToUpdated, gameToUpdated.getId());
        assertNotNull(gameUpdated);
        assertEquals(30,gameToUpdated.getDuration());      
    }

    @Test
    @Transactional
    public void shouldDeleteGame() {
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

   




}
