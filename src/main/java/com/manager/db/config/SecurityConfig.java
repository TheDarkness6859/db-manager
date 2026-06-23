package com.manager.db.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth ->
                    auth.requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler((request, response, authentication) -> {
                            var roles = authentication.getAuthorities();
                            boolean isAdmin = roles.stream()
                                    .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_ADMIN"));

                            if (isAdmin) {
                                response.sendRedirect("/admin/users");
                            } else {
                                response.sendRedirect("/dashboard");
                            }
                        })
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                .logout(logout ->
                        logout.logoutUrl("/auth/logout")
                                .logoutSuccessUrl("/auth/login?logout=true")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                )

                .csrf(AbstractHttpConfigurer::disable
                )
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
