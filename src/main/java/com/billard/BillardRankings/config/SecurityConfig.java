package com.billard.BillardRankings.config;

import com.billard.BillardRankings.service.JwtService;
import com.billard.BillardRankings.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JwtService jwtService;
    private final WorkspaceRepository workspaceRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // 👈 bắt buộc bật CORS để CorsConfig có hiệu lực
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // các GET đều được, POST/PUT/DELETE bị filter chặn trong JwtAuthenticationFilter
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, workspaceRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new WorkspaceAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

