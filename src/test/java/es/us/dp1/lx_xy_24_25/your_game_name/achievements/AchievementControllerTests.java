package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.achievements.Achievement.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@WebMvcTest(controllers = AchievementRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class AchievementControllerTests {

	private static final int TEST_AUTH_ID = 1;
	private static final String BASE_URL = "/api/v1/achievements";

    @SuppressWarnings("unused")
	@Autowired
    private AchievementRestController achievementRestController;

    @MockBean
	private UserService userService;
    
    @MockBean
    private AchievementService achievementService;

    @Autowired
	private MockMvc mockMvc;

    @Autowired
	private ObjectMapper objectMapper;

    private Authorities auth;
	private User user, logged;
    private Achievement achievement;

    @BeforeEach
	void setup() {
		auth = new Authorities();
		auth.setId(TEST_AUTH_ID);
		auth.setAuthority("PLAYER");

        achievement = new Achievement();
        achievement.setId(1);
        achievement.setName("achievement");
        achievement.setThreshold(25);
        achievement.setMetric(Metric.GAMES_PLAYED);

		user = new User();
		user.setId(1);
		user.setUsername("user");
		user.setPassword("password");
		user.setAuthority(auth);

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
	@WithMockUser("player")
    void shouldFindAllAchievements() throws Exception{
        List<Achievement> achievements = createValidAchievements();

		when(this.achievementService.findAll()).thenReturn(achievements);

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[?(@.id == 1)].name").value("achievement1"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("achievement2"));
    }

    @Test
	@WithMockUser("player")
    void shouldFindAllAchievementsByUser() throws Exception {
        List<Achievement> achievements = createValidAchievements();
        user.setAchievements(achievements);

        when(this.userService.findCurrentUser()).thenReturn(user);
        when(this.achievementService.findAchievementByUserId(user.getId())).thenReturn(user.getAchievements());

        mockMvc.perform(get(BASE_URL + "/myAchievement")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[?(@.id == 1)].name").value("achievement1"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("achievement2"));
    }

    @Test
    @WithMockUser("admin")
    void shouldCreateAchievement() throws Exception {
        Achievement aux = new Achievement();
        aux.setId(1);
        aux.setName("achievement");
        aux.setThreshold(25);
        aux.setMetric(Metric.GAMES_PLAYED);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateAchievement() throws Exception {
        achievement.setName("UPDATED");
		achievement.setThreshold(50);

		when(this.achievementService.findAchievement(achievement.getId())).thenReturn(achievement);
		when(this.achievementService.updateAchievement(any(Achievement.class), any(Integer.class))).thenReturn(achievement);

		mockMvc.perform(put(BASE_URL + "/{id}", achievement.getId()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(achievement))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("UPDATED")).andExpect(jsonPath("$.threshold").value(50));
    }

    @Test
    @WithMockUser("admin")
    void shouldNotUpdateAchievement() throws Exception {
        achievement.setName("UPDATED");
		achievement.setThreshold(100);

		when(this.achievementService.findAchievement(achievement.getId())).thenThrow(ResourceNotFoundException.class);
		when(this.achievementService.updateAchievement(any(Achievement.class), any(Integer.class))).thenReturn(achievement);

		mockMvc.perform(put(BASE_URL + "/{id}", achievement.getId()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(achievement))).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("admin")
    void shouldDeleteAchievement() throws Exception {

		when(this.achievementService.findAchievement(achievement.getId())).thenReturn(achievement);
		doNothing().when(this.achievementService).deleteAchievement(achievement.getId());

		mockMvc.perform(delete(BASE_URL + "/{id}", achievement.getId()).with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Achievement deleted!"));
    }

    @Test
    @WithMockUser("admin")
    void shouldNotDeleteAchievement() throws Exception {

        when(this.achievementService.findAchievement(achievement.getId())).thenThrow(ResourceNotFoundException.class);
        doNothing().when(this.achievementService).deleteAchievement(achievement.getId());

        mockMvc.perform(delete(BASE_URL + "/{id}", achievement.getId()).with(csrf())).andExpect(status().isNotFound());
    }

    public List<Achievement> createValidAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        Achievement achievement1 = new Achievement();
        achievement1.setId(1);
        achievement1.setName("achievement1");
        achievement1.setThreshold(25);
        achievement1.setMetric(Metric.GAMES_PLAYED);
        achievements.add(achievement1);

		Achievement achievement2 = new Achievement();
        achievement2.setId(2);
        achievement2.setName("achievement2");
        achievement2.setThreshold(50);
        achievement2.setMetric(Metric.VICTORIES);
        achievements.add(achievement2);
        return achievements;
    }
}
