package com.ladysparks.ttaenggrang.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    // PasswordEncoder를 Bean으로 등록하여 프로젝트 전체에서 사용할 수 있도록 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean isDevMode = "dev".equals(System.getenv("SPRING_PROFILES_ACTIVE"));

        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(auth -> {
                        if (isDevMode) {
                            auth.anyRequest().permitAll(); // 개발 환경에서는 모든 요청 허용
                        } else {
                            // Swagger 경로 허용
                            auth.requestMatchers(
                                            "/v3/api-docs/**",
                                            "/swagger-ui/**",
                                            "/swagger-ui.html",
                                            "/swagger-resources/**",
                                            "/webjars/**",
                                            "/students/login",
                                            "/students/create"
                                    ).permitAll()
                                    // 인증 필요 없는 경로
                                    .requestMatchers("/teachers/**").permitAll()
                                    // 나머지 요청은 인증 필요
                                    .anyRequest().authenticated();
                        }
                    }
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
