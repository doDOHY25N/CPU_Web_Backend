package com.cpu.web.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/login", "/loginProc", "/signup", "/signupProc", "/auth/**", "swagger-ui/**", "/v3/api-docs/**").permitAll()  // 이메일 인증 경로 허용
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/study/**").permitAll()
                .requestMatchers("/post/**").permitAll() //test용 0915준혁
                .anyRequest().authenticated()
        );

        http.formLogin((formLogin) -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/loginProc")
                .successHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"message\": \"Login successful\"}");
                })
                .failureHandler((request, response, exception) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"message\": \"Login failed\"}");
                })
        );

        http.logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/"));

        return http.build();
    }
}