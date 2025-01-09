package es.us.dp1.lx_xy_24_25.your_game_name.notifications;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.NotificationDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.Notification;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.NotificationController;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.NotificationService;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.NotificationType;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@WebMvcTest(controllers = NotificationController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class NotificationControllerTests {

    private static final String BASE_URL = "/api/v1/notifications";

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;

    @SuppressWarnings("unused")
    @Autowired
    private NotificationController notificationController;
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notification;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("user1");
        User sender = new User();
        sender.setId(2);
        sender.setUsername("sender1");

        notification = new Notification();
        notification.setGamecode("ABCDE");
        notification.setId(1);
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setUser(user);
        notification.setSender(sender);
    }

    @Test
    @WithMockUser("player")
    void shouldGetNotificationByUser() throws Exception {
        List<Notification> notifications = new ArrayList<>(List.of(notification));

        when(userService.findUser(anyString())).thenReturn(user);
        when(notificationService.findByUser(any(User.class))).thenReturn(Optional.of(notifications));

        mockMvc.perform(get(BASE_URL + "/user/{userName}", "user1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].senderUsername").value("sender1"))
                .andExpect(jsonPath("$[0].type").value("FRIEND_REQUEST"));
    }

    @Test
    @WithMockUser("player")
    void shouldCreateNotification() throws JsonProcessingException, Exception {
        when(notificationService.saveNotification(any(Notification.class))).thenReturn(notification);

        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(notification))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("player")
    void shouldDeleteNotification() throws Exception {
        NotificationDTO notificationDTO = notification.toDTO();
        when(userService.findUser(anyString())).thenReturn(user);
        when(notificationService.findByUserSender(any(User.class), any(User.class)))
            .thenReturn(Optional.of(List.of(notification)));

        mockMvc.perform(delete(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDTO))).andExpect(status().isNoContent());
        
        verify(notificationService).deleteNotification(anyInt());
    }
}
