package es.us.dp1.lx_xy_24_25.your_game_name.configuration;

import java.util.Arrays;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class SpringSecurityWebAuxTestConfiguration {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetailsImpl ownerActiveUser = new UserDetailsImpl(1, "owner", "password",
        		Arrays.asList(
                        new SimpleGrantedAuthority("OWNER"))
        );

        UserDetailsImpl adminActiveUser = new UserDetailsImpl(1, "admin", "password",
        		Arrays.asList(
                        new SimpleGrantedAuthority("ADMIN"))
        );
        
        UserDetailsImpl vetActiveUser = new UserDetailsImpl(1, "vet", "password",
        		Arrays.asList(
                        new SimpleGrantedAuthority("VET"))
        );

        UserDetailsImpl playerActiveUser = new UserDetailsImpl(1, "player", "password",
        		Arrays.asList(
                        new SimpleGrantedAuthority("PLAYER"))
        );

        return new InMemoryUserDetailsManager(Arrays.asList(
        		ownerActiveUser, adminActiveUser, vetActiveUser, playerActiveUser
        ));
    }
}
