package com.graduationproject.user_service.config;

import com.graduationproject.user_service.filter.JwtAuthenticationFilter;
import com.graduationproject.user_service.filter.ServiceAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private ServiceAuthenticationFilter serviceAuthenticationFilter;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        @Profile("dev")
        public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // .requestMatchers("/api/**").permitAll()
                                                .requestMatchers("/api/users/register").permitAll()
                                                .requestMatchers("/api/users/{id}").authenticated()
                                                .requestMatchers("/api/users/username/{username}").authenticated()
                                                // .requestMatchers("/api/users").authenticated()
                                                .anyRequest().permitAll())
                                .build();
        }

        @Bean
        @Profile("prod")
        public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/users/register").permitAll()
                                                .requestMatchers("/api/users/{id}").hasAnyRole("SERVICE", "USER")
                                                .requestMatchers("/api/users/username/{username}")
                                                .hasAnyRole("SERVICE", "USER")
                                                .requestMatchers("/api/users").hasAnyRole("SERVICE", "USER")
                                                .requestMatchers("/api/users/active").hasAnyRole("SERVICE", "USER")
                                                .anyRequest().denyAll())
                                .addFilterBefore(serviceAuthenticationFilter,
                                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(jwtAuthenticationFilter,
                                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                                .httpBasic(basic -> {
                                })
                                .build();
        }
}
