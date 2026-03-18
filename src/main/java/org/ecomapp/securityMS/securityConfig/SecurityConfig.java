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
                        "/webhooks/stripe"
                ).permitAll()

                // swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()

                // h2
                .requestMatchers("/h2/**").permitAll()

                // product browsing
                .requestMatchers(HttpMethod.GET,
                        "/products/**",
                        "/categories/**",
                        "/reviews/**"
                ).permitAll()

                // admin only
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                // seller only
                .requestMatchers(HttpMethod.POST, "/products/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.PATCH, "/products/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("SELLER")
                .requestMatchers("/orders/sub-orders/*/status", "/orders/sub-orders/me").hasRole("SELLER")
                .requestMatchers("/seller/**").hasRole("SELLER")

                // buyer only
                .requestMatchers(
                        "/cart/**",
                        "/orders/**",
                        "/payments/**"
                ).hasRole("BUYER")

                .requestMatchers(HttpMethod.POST, "/refunds/sub-orders/**").hasRole("BUYER")

                // admin - refund approval
                .requestMatchers(HttpMethod.PATCH, "/refunds/*/status").hasRole("ADMIN")

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

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                        (request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        }
                )
                .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Access Denied\"}");
                        }
                )
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

