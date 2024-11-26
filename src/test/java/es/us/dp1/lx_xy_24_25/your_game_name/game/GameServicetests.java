package es.us.dp1.lx_xy_24_25.your_game_name.game;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
        System.out.println("GameRepository: " + gameRepository);
        System.out.println("GameService: " + gameService);
        //Simulo comportamiento de findAll
        Game g = new Game();
        g.setId(1);
        g.setDuration(20);
        g.setGameCode("ABCDE");
        List<Game> mockGames = List.of(g);
        when(gameRepository.findAll()).thenReturn(mockGames);
        //Llamo al repositorio
        List<Game> games = (List<Game>) gameService.findAll();

        //Compruebo el resultado
        assertEquals(1, games.size());
        assertEquals("ABCDE",games.get(0).getGameCode());
    }


    @Test
    public void shouldFindGame(){
        Game mockGame = new Game();
        mockGame.setId(1);
        mockGame.setDuration(20);
        mockGame.setGameCode("ABCDE");
        Mockito.when(gameRepository.findById(1)).thenReturn(Optional.of(mockGame));

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
        Game mockGame = new Game();
        mockGame.setId(1);
        mockGame.setDuration(20);
        mockGame.setGameCode("ABCDE");
        when(gameRepository.findGameByGameCode("ABCDE")).thenReturn(Optional.of(mockGame));
        
        Game gamebyGameCode = gameService.findGameByGameCode("ABCDE");
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
        Game mockGame = new Game();
        mockGame.setId(1);
        mockGame.setDuration(20);
        mockGame.setGameCode("ABCDE");
        mockGame.setGameState(GameState.WAITING);
        List<Game> joinableGames = List.of(mockGame);
        List<GameState> validStates = List.of(GameState.IN_PROCESS,GameState.WAITING);
        when(gameRepository.findByGameStateIn(validStates)).thenReturn(joinableGames);

        List<Game> games = gameService.findJoinableGames();
        assertFalse(games.isEmpty());
        assertEquals(20, games.get(0).getDuration()); 
    }







    @Test
    public void shouldSaveGame(){
        Game newGame = new Game();
        newGame.setId(1);
        newGame.setDuration(20);;
        when(gameRepository.save(newGame)).thenReturn(newGame);

        Game savedGame = gameService.saveGame(newGame);
        assertNotNull(newGame.getId());
        assertEquals(20, savedGame.getDuration());
    }

    @Test 
    public void shouldUpdateGame(){
        Game g = new Game();
        g.setId(1);
        g.setDuration(10);

        when(gameRepository.save(g)).thenReturn(g);
        Game gameToUpdated = gameService.saveGame(g);

        gameToUpdated.setDuration(20);
        when(gameRepository.findById(1)).thenReturn(Optional.of(g));
        when(gameRepository.save(g)).thenReturn(gameToUpdated);

        Game gameUpdated = gameService.updateGame(gameToUpdated, gameToUpdated.getId());
        assertNotNull(gameUpdated);
        assertEquals(20,gameToUpdated.getDuration());      
    }

    @Test 
    @Transactional
    public void shouldDeleteGame(){
        int count = ((List<Game>) gameService.findAll()).size();
        int idToDelete = 1;
        gameService.deleteGame(idToDelete);
        int finalCount = ((List<Game>) gameService.findAll()).size();
        assertEquals(count-1, finalCount);
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGame(simGame.getId()));
    }




}
