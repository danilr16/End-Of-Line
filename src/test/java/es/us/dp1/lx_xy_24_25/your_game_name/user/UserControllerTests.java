package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.request.LoginRequest;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsImpl;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.UserProfileUpdateDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRestController;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import lombok.With;

/**
 * Test class for the {@link VetController}
 */
@WebMvcTest(controllers = UserRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class UserControllerTests {

	private static final int TEST_USER_ID = 1;
	private static final int TEST_AUTH_ID = 1;
	private static final String BASE_URL = "/api/v1/users";

	@SuppressWarnings("unused")
	@Autowired
	private UserRestController userController;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthoritiesService authService;

	@MockBean
	private PlayerService playerService;

	@MockBean
    private PasswordEncoder encoder;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Authorities auth;
	private User user, logged;

	@BeforeEach
	void setup() {
		auth = new Authorities();
		auth.setId(TEST_AUTH_ID);
		auth.setAuthority("VET");

		user = new User();
		user.setId(1);
		user.setUsername("user");
		user.setPassword("password");
		user.setAuthority(auth);
		user.setFriends(new ArrayList<>());

		when(this.userService.findCurrentUser()).thenReturn(getUserFromDetails(
				(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
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
	@WithMockUser("admin")
	void shouldFindAll() throws Exception {
		User sara = new User();
		sara.setId(2);
		sara.setUsername("Sara");

		User juan = new User();
		juan.setId(3);
		juan.setUsername("Juan");

		when(this.userService.findAll()).thenReturn(List.of(user, sara, juan));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].username").value("user"))
				.andExpect(jsonPath("$[?(@.id == 2)].username").value("Sara"))
				.andExpect(jsonPath("$[?(@.id == 3)].username").value("Juan"));
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAllWithAuthority() throws Exception {
		Authorities aux = new Authorities();
		aux.setId(2);
		aux.setAuthority("AUX");

		User sara = new User();
		sara.setId(2);
		sara.setUsername("Sara");
		sara.setAuthority(aux);

		User juan = new User();
		juan.setId(3);
		juan.setUsername("Juan");
		juan.setAuthority(auth);

		when(this.userService.findAllByAuthority(auth.getAuthority())).thenReturn(List.of(user, juan));

		mockMvc.perform(get(BASE_URL).param("auth", "VET")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[?(@.id == 1)].username").value("user"))
				.andExpect(jsonPath("$[?(@.id == 3)].username").value("Juan"));
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAllAuths() throws Exception {
		Authorities aux = new Authorities();
		aux.setId(2);
		aux.setAuthority("AUX");

		when(this.authService.findAll()).thenReturn(List.of(auth, aux));

		mockMvc.perform(get(BASE_URL + "/authorities")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[?(@.id == 1)].authority").value("VET"))
				.andExpect(jsonPath("$[?(@.id == 2)].authority").value("AUX"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnUser() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_USER_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_USER_ID))
				.andExpect(jsonPath("$.username").value(user.getUsername()))
				.andExpect(jsonPath("$.authority.authority").value(user.getAuthority().getAuthority()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUser() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_USER_ID)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldCreateUser() throws Exception {
		User aux = new User();
		aux.setUsername("Prueba");
		aux.setPassword("Prueba");
		aux.setAuthority(auth);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateUser() throws Exception {
		user.setUsername("UPDATED");
		user.setPassword("CHANGED");

		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("UPDATED")).andExpect(jsonPath("$.password").value("CHANGED"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUpdateUser() throws Exception {
		user.setUsername("UPDATED");
		user.setPassword("UPDATED");

		when(this.userService.findUser(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldDeleteOtherUser() throws Exception {
		logged.setId(2);

		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		doNothing().when(this.userService).deleteUser(TEST_USER_ID);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_USER_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("User deleted!"));
	}

	@Test
	@WithMockUser("admin")
	void shouldNotDeleteLoggedUser() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		doNothing().when(this.userService).deleteUser(TEST_USER_ID);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_USER_ID).with(csrf())).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@Test
	@WithMockUser("player")
	void shouldReturnCurrentUser() throws Exception {
		when(userService.findCurrentUser()).thenReturn(user);

		mockMvc.perform(get(BASE_URL + "/currentUser")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_USER_ID))
				.andExpect(jsonPath("$.username").value(user.getUsername()));

		verify(userService, times(1)).findCurrentUser();
	}

	@Test
	@WithMockUser("player")
	void shouldReturnAllGames() throws Exception {
		List<Game> games = createValidGames();
		when(userService.findCurrentUser()).thenReturn(user);
		when(userService.findAllGamesByUserHost(any(User.class))).thenReturn(games);

		mockMvc.perform(get(BASE_URL + "/games"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$[?(@.gameCode == 'ZYXWV')].gameMode").value("PUZZLE_SINGLE"))
				.andExpect(jsonPath("$[?(@.gameCode == 'ZYXWV')].host.username").value(user.getUsername()));

		verify(userService, times(1)).findCurrentUser();
		verify(userService, times(1)).findAllGamesByUserHost(any(User.class));
	}

	@Test
	@WithMockUser("player")
	void shoulUpdateProfile() throws Exception {
		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setNewUsername("UPDATED");
		dto.setNewImage("IMG_UPDATED");
		when(userService.findCurrentUser()).thenReturn(user);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);

		mockMvc.perform(patch(BASE_URL + "/myProfile").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("UPDATED"))
				.andExpect(jsonPath("$.image").value("IMG_UPDATED"));

		verify(userService, times(1)).findCurrentUser();
		verify(userService, times(1)).updateUser(any(User.class), any(Integer.class));
	}

	@Test
	@WithMockUser("player")
	void shoulNotUpdateProfile() throws Exception {
		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setNewUsername("UPDATED");
		dto.setNewImage("IMG_UPDATED");
		when(userService.findCurrentUser()).thenThrow(AccessDeniedException.class);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);

		mockMvc.perform(patch(BASE_URL + "/myProfile").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		verify(userService, times(1)).findCurrentUser();
	}

	@Test
	@WithMockUser("player")
	void shoulNotUpdateProfileAnotherUserWithThatName() throws Exception {
		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setNewUsername("UPDATED");
		dto.setNewImage("IMG_UPDATED");
		when(userService.findCurrentUser()).thenReturn(user);
		when(userService.existsUser(anyString())).thenReturn(true);

		mockMvc.perform(patch(BASE_URL + "/myProfile").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
				.andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("There already is a user with that username!")));

		verify(userService, times(1)).findCurrentUser();
		verify(userService).existsUser(anyString());
	}

	@Test
	@WithMockUser("player")
	void shouldUpdatePassword() throws Exception {
		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setOldPasswordDTO("password");
		dto.setNewPasswordDTO("newPassword");
		when(userService.findCurrentUser()).thenReturn(user);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);
		when(this.encoder.matches(any(String.class), any(String.class))).thenReturn(user.getPassword().equals(dto.getOldPasswordDTO()));
		when(this.encoder.encode(any(String.class))).thenReturn(dto.getNewPasswordDTO());

		mockMvc.perform(patch(BASE_URL + "/myProfile/update-password").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.password").value("newPassword"));

		verify(userService, times(1)).findCurrentUser();
		verify(userService, times(1)).updateUser(any(User.class), any(Integer.class));
		verify(encoder, times(1)).matches(any(String.class), any(String.class));
		verify(encoder, times(1)).encode(any(String.class));
	}

	@Test
	@WithMockUser("player")
	void shouldNotUpdateSamePassword() throws Exception {
		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setOldPasswordDTO("password");
		dto.setNewPasswordDTO("password");
		when(userService.findCurrentUser()).thenReturn(user);
		when(this.encoder.matches(any(String.class), any(String.class))).thenReturn(user.getPassword().equals(dto.getOldPasswordDTO()));

		mockMvc.perform(patch(BASE_URL + "/myProfile/update-password").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		verify(userService, times(1)).findCurrentUser();
		verify(encoder, times(1)).matches(any(String.class), any(String.class));
	}

	@Test
	@WithMockUser("player")
	void shouldUpdateIncorrectPassword() throws Exception {
		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setOldPasswordDTO("BAD_PASSWORD");
		dto.setNewPasswordDTO("newPassword");
		when(userService.findCurrentUser()).thenReturn(user);
		when(this.encoder.matches(any(String.class), any(String.class))).thenReturn(user.getPassword().equals(dto.getOldPasswordDTO()));

		mockMvc.perform(patch(BASE_URL + "/myProfile/update-password").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		verify(userService, times(1)).findCurrentUser();
		verify(encoder, times(1)).matches(any(String.class), any(String.class));
	}

	@Test
	@WithMockUser("player")
	void shouldNotUpdatePasswordWithoutOldPassword() throws Exception {
		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setOldPasswordDTO(null);
		dto.setNewPasswordDTO("newPassword");
		when(userService.findCurrentUser()).thenReturn(user);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);

		mockMvc.perform(patch(BASE_URL + "/myProfile/update-password").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
				.andExpect(result -> assertTrue(result.getResolvedException().getMessage().equals("All fields are required!")));

		verify(userService, times(1)).findCurrentUser();
	}

	public Player createValidPlayer() {
		Hand hand = new Hand();
		hand.setId(1);
		hand.setCards(new ArrayList<>());
		Player player = new Player();
		player.setHand(hand);
		player.setUser(user);
		player.setPackCards(new ArrayList<>());
		return player;
	}

	public List<Game> createValidGames() {
		List<Game> games = new ArrayList<>();
		Player player = createValidPlayer();
		Game game = new Game();
		game.setId(1);
		game.setGameCode("ZYXWV");
		game.setGameMode(GameMode.PUZZLE_SINGLE);
		game.setHost(user);
		game.setNumPlayers(1);
		game.setIsPublic(false);
		game.setPlayers(List.of(player));
		game.setSpectators(new ArrayList<>());
		games.add(game);
		return games;
	}

	private static List<User> createValidFriendList(User user) {
		List<User> users = new ArrayList<>();
		user.setId(16);
		user.setUsername("friend1");
		user.setPassword("password");
		user.setAuthority(new Authorities());
		users.add(user);
		return users;
	}
	
	@Test
	@WithMockUser("player")
	void shouldReturnFriends() throws Exception {
		User mockUser = mock(User.class);
		when(userService.findCurrentUser()).thenReturn(mockUser);
		when(mockUser.getFriends()).thenReturn(createValidFriendList(new User()));

		mockMvc.perform(get(BASE_URL + "/friends"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$[0].username").value("friend1"));

		verify(userService, times(1)).findCurrentUser();
		verify(mockUser, times(1)).getFriends();
	}

	@Test
    @WithMockUser("player")
    void shouldAddFriend() throws Exception {
        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setUsername("currentUser");
        currentUser.setFriends(new ArrayList<>());
		currentUser.setAuthority(new Authorities());

        User newFriend = new User();
        newFriend.setId(2);
        newFriend.setUsername("newFriend");
        newFriend.setFriends(new ArrayList<>());
		newFriend.setAuthority(new Authorities());

        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(userService.findUser("newFriend")).thenReturn(newFriend);

		when(userService.updateUser(currentUser, currentUser.getId())).thenReturn(currentUser);
		when(userService.updateUser(newFriend, newFriend.getId())).thenReturn(newFriend);

        MvcResult result = mockMvc.perform(patch(BASE_URL +"/addFriend/{username}",newFriend.getUsername()).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) 
            	.andExpect(jsonPath("$[0].username").value("currentUser")) 
            	.andExpect(jsonPath("$[1].username").value("newFriend"))
				.andReturn();
	
		String jsonResponse = result.getResponse().getContentAsString();
		System.out.println("Response JSON: " + jsonResponse);

        verify(userService, times(1)).findCurrentUser();
        verify(userService, times(1)).findUser("newFriend");
        verify(userService, times(1)).updateUser(currentUser, currentUser.getId());
        verify(userService, times(1)).updateUser(newFriend, newFriend.getId());
    }

	@Test
	@WithMockUser("player")
	void should_not_add_yourself_as_a_friend() throws Exception {

		User currentUser = new User();
        currentUser.setId(1);
        currentUser.setUsername("currentUser");
        currentUser.setFriends(new ArrayList<>());
		currentUser.setAuthority(new Authorities());

		User newFriend = currentUser;


		when(userService.findCurrentUser()).thenReturn(currentUser);
        when(userService.findUser(newFriend.getUsername())).thenReturn(newFriend);
		

		when(userService.updateUser(currentUser, currentUser.getId())).thenReturn(currentUser);
		when(userService.updateUser(newFriend, newFriend.getId())).thenReturn(newFriend);
		
		mockMvc.perform(patch(BASE_URL + "/addFriend/{username}",newFriend.getUsername()).with(csrf()))
				.andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
				.andExpect(result -> 
                assertEquals("You can't add yourself as a friend!", 
                             result.getResolvedException().getMessage()));

	}


	@Test
	@WithMockUser("player")
	public void shouldThrowAccessDeniedIfAlreadyFriends() throws Exception {
		
		User currentUser = new User();
		currentUser.setId(1);
		currentUser.setUsername("player1");
		currentUser.setFriends(new ArrayList<>());

		
		User newFriend = new User();
		newFriend.setId(2);
		newFriend.setUsername("newFriend");
		newFriend.setFriends(new ArrayList<>());

		
		currentUser.getFriends().add(newFriend);

		
		when(userService.findCurrentUser()).thenReturn(currentUser);
		when(userService.findUser("newFriend")).thenReturn(newFriend);

		when(userService.updateUser(currentUser, currentUser.getId())).thenReturn(currentUser);
		when(userService.updateUser(newFriend, newFriend.getId())).thenReturn(newFriend);

		
		mockMvc.perform(patch(BASE_URL + "/addFriend/{username}", "newFriend").with(csrf()))
				.andExpect(status().isForbidden()) 
				.andExpect(result -> 
					assertTrue(result.getResolvedException() instanceof AccessDeniedException))
				.andExpect(result -> 
				assertEquals("You are already friends with this user!", 
								result.getResolvedException().getMessage()));

		
		verify(userService, times(1)).findCurrentUser();
		verify(userService, times(1)).findUser("newFriend");
		verifyNoMoreInteractions(userService); 
	}

	@Test
	@WithMockUser("player")
	public void shouldReturnEmpty() throws Exception {
		User currentUser = new User();
		currentUser.setId(1);
		currentUser.setUsername("player1");

		when(userService.findCurrentUser()).thenReturn(currentUser);
		when(userService.findAllGamesWithUser(currentUser)).thenReturn(Collections.emptyList());

		mockMvc.perform(get(BASE_URL + "/gamesAsplayer").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isEmpty());

		verify(userService, times(1)).findCurrentUser();
		verify(userService, times(1)).findAllGamesWithUser(currentUser);
	}

	@Test
	@WithMockUser("player")
	void shouldFindUserDTO() throws Exception {
		User currentUser = new User();
		currentUser.setId(1);
		currentUser.setUsername("player1");
		currentUser.setImage("fake_img");
		currentUser.setFriends(new ArrayList<>());

		when(userService.findCurrentUser()).thenReturn(currentUser);
		
		mockMvc.perform(get(BASE_URL + "/currentUserDTO").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.username").value("player1"))
			.andExpect(jsonPath("$.image").value("fake_img"));

		verify(userService, times(1)).findCurrentUser();
	}
}
