package recipes.table.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import recipes.table.filters.JwtAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/recipes").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/recipes/me").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/recipes/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/recipes/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/recipes/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*/recipes").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/recipes").authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}