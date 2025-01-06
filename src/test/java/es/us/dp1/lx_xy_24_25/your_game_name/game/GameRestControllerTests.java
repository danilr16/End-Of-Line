package es.us.dp1.lx_xy_24_25.your_game_name.game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PowerType;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.team.Team;
import es.us.dp1.lx_xy_24_25.your_game_name.team.TeamService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@WebMvcTest(controllers = GameRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class GameRestControllerTests {

    private static final String BASE_URL = "/api/v1/games";

    @SuppressWarnings("unused")
    @Autowired
    private GameRestController gameRestController;

    @MockBean
    private GameService gameService;

    @MockBean
    private UserService userService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private HandService handService;

    @MockBean
    private TableCardService tableCardService;

    @MockBean
    private PackCardService packCardService;

    @MockBean
    private TeamService teamService;

    @MockBean
    private CardService cardService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
	private ObjectMapper objectMapper;

    private User user;
    private Authorities auth;
    private Game game;
    private Player player1;

    @BeforeEach
    void setUp() {
        auth = new Authorities();
        auth.setId(1);
        auth.setAuthority("PLAYER");

        user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        user.setAuthority(auth);
        user.setFriends(new ArrayList<>());

        when(this.userService.findCurrentUser()).thenReturn(user);
        
        //Creamos jugadores del juego
        List<Player> players = new ArrayList<>();
        player1 = new Player();
        player1.setId(1);
        player1.setUser(user);
        player1.setState(PlayerState.PLAYING);
        player1.setPackCards(new ArrayList<>());
        player1.setUsedPowers(new ArrayList<>());
        player1.setEnergy(3);
        player1.setEnergyUsedThisRound(false);
        player1.setCardsPlayedThisTurn(0);
        player1.setHandChanged(false);
        players.add(player1);
        List<Card> cards = GameServiceTests.simCreate25Cards(player1);
        Hand hand = new Hand();
        hand.setNumCards(5);
        hand.setCards(new ArrayList<>(cards.subList(0, 5)));
        player1.setHand(hand);
        PackCard packCard = new PackCard();
        packCard.setCards(new ArrayList<>(cards.subList(5, cards.size())));
        packCard.setNumCards(20);

        User newUser = new User();
        newUser.setId(2);
        newUser.setUsername("user2");
        newUser.setFriends(new ArrayList<>());
        Player player2 = new Player();
        player2.setId(2);
        player2.setState(PlayerState.PLAYING);
        player2.setUser(newUser);
        player2.setPackCards(new ArrayList<>());
        players.add(player2);

        //Creamos tablero y juego
        TableCard tableCard = new TableCard();
        tableCard.setRows(new ArrayList<>());
        game = new Game();
        game.setHost(user);
        game.setGameCode("ABCDE");
        game.setGameMode(GameMode.VERSUS);
        game.setGameState(GameState.IN_PROCESS);
        game.setId(1);
        game.setNumPlayers(2);
        game.setPlayers(players);
        game.setTable(tableCard);
        game.setSpectators(new ArrayList<>());
        game.setNTurn(3);
        game.setTurn(1);
        List<ChatMessage> chat = new ArrayList<>();
        ChatMessage ch = new ChatMessage();
        ch.setGameCode(game.getGameCode());
        ch.setUserName(user.getUsername());
        ch.setMessageString("hi");
        chat.add(ch);
        game.setChat(chat);
    }
    
    @Test
    @WithMockUser("player")
    void shouldFindAll() throws Exception {
        when(gameService.findAll()).thenReturn(List.of(game));

        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].numPlayers").value(2))
                .andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].gameMode").value("VERSUS"))
                .andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].gameState").value("IN_PROCESS"));
        verify(gameService).findAll();
    }

    private Hand createValidHand() {
        Hand hand = new Hand();
        hand.setCards(new ArrayList<>());
        hand.setNumCards(0);
        return hand;
    }

    private Player createValidPlayer(User user, Hand hand) {
        Player player = new Player();
        player.setUser(user);
        player.setHand(hand);
        return player;
    }

    @Test
    @WithMockUser("player")
    void shouldCreateGame() throws Exception {
        Hand hand = createValidHand();
        Player player = createValidPlayer(user, hand);
        when(handService.saveVoidHand()).thenReturn(hand);
        when(playerService.saveUserPlayerbyUser(any(User.class), any(Hand.class))).thenReturn(player);
        when(gameService.saveGame(any(Game.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        mockMvc.perform(post(BASE_URL).with(csrf())
            .param("isPublic", "true")
            .param("numPlayers", "2")
            .param("gameMode", "VERSUS")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.host.username").value("user"))
            .andExpect(jsonPath("$.players.size()").value(1))
            .andExpect(jsonPath("$.numPlayers").value(2))
            .andExpect(jsonPath("$.isPublic").value(true))
            .andExpect(jsonPath("$.gameMode").value("VERSUS"));
        verify(userService).findCurrentUser();
        verify(handService).saveVoidHand();
        verify(playerService).saveUserPlayerbyUser(any(User.class), any(Hand.class));
        verify(gameService).saveGame(any(Game.class));
    }

    private Team createTeam(Player p1, Player p2) {
        Team team = new Team();
            team.setId(1);
            team.setPlayer1(p1);
            team.setPlayer2(p2);
        return team;
    }

    @Test
    @WithMockUser("player")
    void shouldCreateGameWithTeamBattle() throws Exception {
        Hand hand = createValidHand();
        Player player = createValidPlayer(user, hand);
        when(handService.saveVoidHand()).thenReturn(hand);
        when(playerService.saveUserPlayerbyUser(any(User.class), any(Hand.class))).thenReturn(player);
        when(gameService.saveGame(any(Game.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });
        when(teamService.saveTeamByPlayers(any(Player.class), any())).thenAnswer(invocation -> {
            return createTeam(invocation.getArgument(0), invocation.getArgument(1));
        });

        mockMvc.perform(post(BASE_URL).with(csrf())
            .param("isPublic", "true")
            .param("numPlayers", "2")
            .param("gameMode", "TEAM_BATTLE")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.host.username").value("user"))
            .andExpect(jsonPath("$.players.size()").value(1))
            .andExpect(jsonPath("$.numPlayers").value(2))
            .andExpect(jsonPath("$.isPublic").value(true))
            .andExpect(jsonPath("$.gameMode").value("TEAM_BATTLE"))
            .andExpect(jsonPath("$.teams.size()").value(1))
            .andExpect(jsonPath("$.teams[?(@.id == 1)].player1.user.username").value("user"));
        verify(userService).findCurrentUser();
        verify(handService).saveVoidHand();
        verify(playerService).saveUserPlayerbyUser(any(User.class), any(Hand.class));
        verify(teamService).saveTeamByPlayers(any(Player.class), any());
        verify(gameService).saveGame(any(Game.class));
    }

    @Test
    @WithMockUser("player")
    void shouldFindJoinableGames() throws Exception {
        when(gameService.findJoinableGames()).thenReturn(List.of(game));

        mockMvc.perform(get(BASE_URL + "/current")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].numPlayers").value(2))
                .andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].gameMode").value("VERSUS"))
                .andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].gameState").value("IN_PROCESS"));
        verify(gameService).findJoinableGames();
    }

    @Test
    @WithMockUser("player")
    void shouldFindGameChat() throws Exception {
        when(gameService.getGameChat(anyString())).thenReturn(game.getChat());

        mockMvc.perform(get(BASE_URL + "/{gameCode}/chat", "ABCDE"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[-1].userName").value("user"))
                .andExpect(jsonPath("$[-1].messageString").value("hi"));
        verify(gameService).getGameChat(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldUseAccelerate() throws Exception {
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        when(gameService.manageUseOfEnergy(any(PowerType.class), any(Player.class), any(Game.class))).thenCallRealMethod();

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/useEnergy", "ABCDE").with(csrf())
                .param("powerType", "ACCELERATE")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful());
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
        verify(gameService).manageUseOfEnergy(any(PowerType.class), any(Player.class), any(Game.class));
        verify(gameService).useAccelerate(any(Player.class));
    }

    @Test
    @WithMockUser("player")
    void shouldNotUseAccelerateNotInGame() throws Exception {
        User newUser = new User();
        when(userService.findCurrentUser()).thenReturn(newUser);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/useEnergy", "ABCDE").with(csrf())
                .param("powerType", "ACCELERATE")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't use energy, because you aren't in this game")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldNotUseAccelerateGameWaiting() throws Exception {
        game.setGameState(GameState.WAITING);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/useEnergy", "ABCDE").with(csrf())
                .param("powerType", "ACCELERATE")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't use energy right now")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldNotUseAcceleratePlayerUsedPower() throws Exception {
        player1.setEnergyUsedThisRound(true);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/useEnergy", "ABCDE").with(csrf())
                .param("powerType", "ACCELERATE")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't use energy right now")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldNotUseAccelerateNotYourTurn() throws Exception {
        game.setTurn(2);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/useEnergy", "ABCDE").with(csrf())
                .param("powerType", "ACCELERATE")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't use energy right now")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldChangeInitialHand() throws Exception {
        game.setNTurn(1);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        when(playerService.findPlayer(anyInt())).thenReturn(player1);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/changeInitialHand", "ABCDE").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful());
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
        verify(playerService).findPlayer(anyInt());
        verify(gameService).changeInitialHand(any(Player.class));
    }

    @Test
    @WithMockUser("player")
    void shouldCantChangeInitialHandNotInGame() throws Exception {
        game.setNTurn(1);
        User newUser = new User();
        when(userService.findCurrentUser()).thenReturn(newUser);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        when(playerService.findPlayer(anyInt())).thenReturn(player1);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/changeInitialHand", "ABCDE").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't change your hand, because you aren't in this game")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldCantChangeInitialHandGameWaiting() throws Exception {
        game.setNTurn(1);
        game.setGameState(GameState.WAITING);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        when(playerService.findPlayer(anyInt())).thenReturn(player1);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/changeInitialHand", "ABCDE").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't change your hand, because this game isn't in process")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldCantChangeInitialHandTurnNot1() throws Exception {
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        when(playerService.findPlayer(anyInt())).thenReturn(player1);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/changeInitialHand", "ABCDE").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't change your hand")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldCantChangeInitialHandChangedPreviously() throws Exception {
        game.setNTurn(1);
        player1.setHandChanged(true);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        when(playerService.findPlayer(anyInt())).thenReturn(player1);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/changeInitialHand", "ABCDE").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't change your hand")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldSendMessage() throws Exception {
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        ChatMessage cm = new ChatMessage();
        cm.setGameCode("ABCDE");
        cm.setMessageString("prueba");
        cm.setUserName(user.getUsername());
        when(gameService.updateGame(any(Game.class))).thenReturn(game);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/chat", "ABCDE").with(csrf())
                .content(objectMapper.writeValueAsString(cm))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[-1].messageString").value("prueba"))
                .andExpect(jsonPath("$[-1].userName").value("user"));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
        verify(gameService).updateGame(any(Game.class));
    }

    @Test
    @WithMockUser("player")
    void shouldNotSendMessageBadUserName() throws Exception {
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        ChatMessage cm = new ChatMessage();
        cm.setGameCode("ABCDE");
        cm.setMessageString("prueba");
        cm.setUserName("badUserName");
        when(gameService.updateGame(any(Game.class))).thenReturn(game);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/chat", "ABCDE").with(csrf())
                .content(objectMapper.writeValueAsString(cm))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't chat in this room")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }

    @Test
    @WithMockUser("player")
    void shouldNotSendMessageNotInGame() throws Exception {
        User newUser = new User();
        when(userService.findCurrentUser()).thenReturn(newUser);
        when(gameService.findGameByGameCode(anyString())).thenReturn(game);
        ChatMessage cm = new ChatMessage();
        cm.setGameCode("ABCDE");
        cm.setMessageString("prueba");
        cm.setUserName(user.getUsername());
        when(gameService.updateGame(any(Game.class))).thenReturn(game);

        mockMvc.perform(patch(BASE_URL + "/{gameCode}/chat", "ABCDE").with(csrf())
                .content(objectMapper.writeValueAsString(cm))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("You can't chat in this room")));
        verify(userService).findCurrentUser();
        verify(gameService).findGameByGameCode(anyString());
    }
}
