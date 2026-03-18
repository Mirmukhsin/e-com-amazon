package org.ecomapp.securityMS.securityConfig;

import lombok.RequiredArgsConstructor;
import org.ecomapp.securityMS.jwtConfig.JWTAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity()
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable);


        http.authorizeHttpRequests(authorize -> authorize
                // public
                .requestMatchers(
                        "/auth/register",
                        "/auth/login",
                        "/webhook/stripe"
                ).permitAll()

                // product browsing
                .requestMatchers(HttpMethod.GET,
                        "/product/**",
                        "/category/**",
                        "/review/**"
                ).permitAll()

                // swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll()

                // admin only
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // seller only
                .requestMatchers(HttpMethod.POST, "/product").hasRole("SELLER")
                .requestMatchers(HttpMethod.PATCH, "/product/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.DELETE, "/product/**").hasRole("SELLER")
                .requestMatchers("/sub/order/**/status").hasRole("SELLER")

                // buyer only
                .requestMatchers(
                        "/cart/**",
                        "/order/**",
                        "/payment/**",
                        "/refund/**request/**"
                ).hasRole("BUYER")

                // admin or seller
                .requestMatchers("/refund/**/status").hasRole("ADMIN")

                .anyRequest().authenticated());

        http.sessionManagement(session -> session
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
        );

        http.userDetailsService(userDetailsService);

        http.addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }
}

