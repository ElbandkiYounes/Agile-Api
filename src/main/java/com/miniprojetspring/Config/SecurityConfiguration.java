package com.miniprojetspring.config;

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
    private static final String API_PATTERN = "/api/**";
    private static final String ROLE_PRODUCT_OWNER = "PRODUCT_OWNER";
    private static final String ROLE_QUALITY_ASSURANCE = "QUALITY_ASSURANCE";

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 1. Public endpoints - no authentication required
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 2. Role-specific permissions (most specific first)
                        // SCRUM_MASTER specific permissions
                        .requestMatchers(HttpMethod.POST, "/api/projects/invite").hasAnyAuthority(ROLE_PRODUCT_OWNER, "SCRUM_MASTER")

                        // DEVELOPER specific permissions
                        .requestMatchers(HttpMethod.PUT, "/api/user-stories/**").hasAnyAuthority(ROLE_PRODUCT_OWNER, "DEVELOPER")

                        // QUALITY_ASSURANCE specific permissions for test cases
                        .requestMatchers(HttpMethod.POST, "/api/user-stories/*/test-cases").hasAnyAuthority(ROLE_PRODUCT_OWNER, ROLE_QUALITY_ASSURANCE)
                        .requestMatchers(HttpMethod.PUT, "/api/test-cases/**").hasAnyAuthority(ROLE_PRODUCT_OWNER, ROLE_QUALITY_ASSURANCE)
                        .requestMatchers(HttpMethod.DELETE, "/api/test-cases/**").hasAnyAuthority(ROLE_PRODUCT_OWNER, ROLE_QUALITY_ASSURANCE)

                        // 3. HTTP method-based permissions
                        // READ operations (GET) accessible to all authenticated users
                        .requestMatchers(HttpMethod.GET, API_PATTERN).authenticated()

                        // WRITE operations (POST, PUT, DELETE) for Product Owner only
                        .requestMatchers(HttpMethod.POST, API_PATTERN).hasAuthority(ROLE_PRODUCT_OWNER)
                        .requestMatchers(HttpMethod.PUT, API_PATTERN).hasAuthority(ROLE_PRODUCT_OWNER)
                        .requestMatchers(HttpMethod.DELETE, API_PATTERN).hasAuthority(ROLE_PRODUCT_OWNER)

                        // 4. Default: require authentication for any other request
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
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

        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}