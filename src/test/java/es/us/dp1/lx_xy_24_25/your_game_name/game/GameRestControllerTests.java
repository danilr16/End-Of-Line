package es.us.dp1.lx_xy_24_25.your_game_name.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
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

    private User user, logged;
    private Authorities auth;
    private Game game;

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

		when(this.userService.findCurrentUser()).thenReturn(getUserFromDetails(
				(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        
        //Creamos jugadores del juego
        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        player1.setId(1);
        player1.setUser(user);
        player1.setState(PlayerState.PLAYING);
        player1.setPackCards(new ArrayList<>());
        players.add(player1);
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
        List<ChatMessage> chat = new ArrayList<>();
        ChatMessage ch = new ChatMessage();
        ch.setGameCode(game.getGameCode());
        ch.setUserName(user.getUsername());
        ch.setMessageString("hi");
        chat.add(ch);
        game.setChat(chat);
    }

    private User getUserFromDetails(UserDetails details) {
		logged = new User();
		logged.setUsername(details.getUsername());
		logged.setPassword(details.getPassword());
		Authorities aux = new Authorities();
		for (GrantedAuthority auth : details.getAuthorities()) {
			aux.setAuthority(auth.getAuthority());
		}
		logged.setAuthority(aux);
		return logged;
	}
    
    @Test
    @WithMockUser("player")
    void shouldFindAll() throws Exception {
        when(gameService.findAll()).thenReturn(List.of(game));

        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].numPlayers").value(2))
				.andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].gameMode").value("VERSUS"))
				.andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].gameState").value("IN_PROCESS"));
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
            .andExpect(jsonPath("$.host.username").value("player"))
            .andExpect(jsonPath("$.players.size()").value(1))
            .andExpect(jsonPath("$.numPlayers").value(2))
            .andExpect(jsonPath("$.isPublic").value(true))
            .andExpect(jsonPath("$.gameMode").value("VERSUS"));
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
            .andExpect(jsonPath("$.host.username").value("player"))
            .andExpect(jsonPath("$.players.size()").value(1))
            .andExpect(jsonPath("$.numPlayers").value(2))
            .andExpect(jsonPath("$.isPublic").value(true))
            .andExpect(jsonPath("$.gameMode").value("TEAM_BATTLE"))
            .andExpect(jsonPath("$.teams.size()").value(1))
            .andExpect(jsonPath("$.teams[?(@.id == 1)].player1.user.username").value("user"));
    }

    @Test
    @WithMockUser("player")
    void shouldFindJoinableGames() throws Exception {
        when(gameService.findJoinableGames()).thenReturn(List.of(game));

        mockMvc.perform(get(BASE_URL + "/current")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].numPlayers").value(2))
				.andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].gameMode").value("VERSUS"))
				.andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].gameState").value("IN_PROCESS"));
    }

    @Test
    @WithMockUser("player")
    void shouldFindGameChat() throws Exception {
        when(gameService.getGameChat(anyString())).thenReturn(game.getChat());

        mockMvc.perform(get(BASE_URL + "/{gameCode}/chat", "ABCDE"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].userName").value("user"))
                .andExpect(jsonPath("$[?(@.gameCode == 'ABCDE')].messageString").value("hi"));
    }
}
