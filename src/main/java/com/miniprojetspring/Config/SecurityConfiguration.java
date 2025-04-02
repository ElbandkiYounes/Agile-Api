package com.miniprojetspring.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        // Allow GET requests for all authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                        // Product owner has full access to all endpoints
                        .requestMatchers("/api/**").hasAnyAuthority("PRODUCT_OWNER")
                        // SCRUM_MASTER can invite users
                        .requestMatchers(HttpMethod.POST, "/api/projects/invite").hasAnyAuthority("PRODUCT_OWNER", "SCRUM_MASTER")
                        // DEVELOPER can edit user stories
                        .requestMatchers(HttpMethod.PUT, "/api/user-stories/**").hasAnyAuthority("PRODUCT_OWNER", "DEVELOPER")
                        // QUALITY_ASSURANCE can handle test cases
                        .requestMatchers(HttpMethod.POST, "/api/user-stories/*/test-cases").hasAnyAuthority("PRODUCT_OWNER", "QUALITY_ASSURANCE")
                        .requestMatchers(HttpMethod.PUT, "/api/test-cases/**").hasAnyAuthority("PRODUCT_OWNER", "QUALITY_ASSURANCE")
                        .requestMatchers(HttpMethod.DELETE, "/api/test-cases/**").hasAnyAuthority("PRODUCT_OWNER", "QUALITY_ASSURANCE")
                        // All other endpoints require detailed authorization at service level
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}