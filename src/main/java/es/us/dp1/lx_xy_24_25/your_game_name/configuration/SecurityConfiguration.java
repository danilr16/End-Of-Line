package es.us.dp1.lx_xy_24_25.your_game_name.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthEntryPointJwt;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthTokenFilter;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	DataSource dataSource;

	private static final String ADMIN = "ADMIN";
	

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		http
			.cors(withDefaults())		
			.csrf(AbstractHttpConfigurer::disable)		
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))			
			.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
			.exceptionHandling((exepciontHandling) -> exepciontHandling.authenticationEntryPoint(unauthorizedHandler))			
			
			.authorizeHttpRequests(authorizeRequests ->	authorizeRequests
			.requestMatchers("/api/v1/developers").permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/resources/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/webjars/**")).permitAll() 
			.requestMatchers(AntPathRequestMatcher.antMatcher("/static/**")).permitAll() 
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-resources/**")).permitAll()						
			.requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/oups")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui.html")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()												
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/developers")).permitAll()												
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/plan")).permitAll()
			.requestMatchers(HttpMethod.GET, "/api/v1/users/games").authenticated()
			.requestMatchers(HttpMethod.PATCH, "/api/v1/users/myProfile").authenticated()
			.requestMatchers(HttpMethod.PATCH, "/api/v1/users/myProfile/update-password").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/v1/users/currentUser").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/v1/users/currentUserDTO").authenticated()
			.requestMatchers(HttpMethod.PATCH, "/api/v1/users/addFriend/**").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/v1/users/friends").authenticated()
			.requestMatchers(HttpMethod.GET,"/api/v1/users/gamesAsplayer").authenticated()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/users/**")).hasAuthority(ADMIN)
			.requestMatchers(HttpMethod.GET, "/api/v1/games").hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/games/**")).authenticated()
			.requestMatchers(HttpMethod.GET, "/api/v1/achievements").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/v1/achievements/myAchievement").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/v1/achievements/check").authenticated()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/achievements/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/statistics")).authenticated()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/ws/**")).permitAll()
			.anyRequest().authenticated())					
			
			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);		
		return http.build();
	}

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}	


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
}
