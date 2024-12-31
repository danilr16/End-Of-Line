package es.us.dp1.lx_xy_24_25.your_game_name.developers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.FilterType;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DevelopersController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class DevelopersControllerTests {

    private static final String BASE_URL = "/api/v1/developers";

    @Autowired
	private MockMvc mockMvc;

    @Test
    @WithMockUser("player")
    void shouldReturnDevelopers() throws Exception {
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(6))
            .andExpect(jsonPath("$[?(@.name == 'Estrella')].email").value("estangpos@alum.us.es"))
            .andExpect(jsonPath("$[?(@.name == 'Ivamirbal')].email").value("ivamirbal@alum.us.es"))
            .andExpect(jsonPath("$[?(@.name == 'Daniel')].email").value("danlopram@alum.us.es"))
            .andExpect(jsonPath("$[?(@.name == 'Diego')].email").value("dieterher@alum.us.es"))
            .andExpect(jsonPath("$[?(@.name == 'AaronMA')].email").value("aarmayans@alum.us.es"))
            .andExpect(jsonPath("$[?(@.name == 'Samuel')].email").value("samgraoli@alum.us.es"));
    }
}
