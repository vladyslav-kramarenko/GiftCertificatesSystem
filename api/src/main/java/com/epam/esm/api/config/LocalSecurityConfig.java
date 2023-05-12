package com.epam.esm.api.config;

import com.epam.esm.api.util.JwtAuthenticationFilter;
import com.epam.esm.core.service.impl.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Objects;

@Profile("dev")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class LocalSecurityConfig {
    private final JwtTokenService jwtTokenService;
    @Autowired
    public LocalSecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = Objects.requireNonNull(jwtTokenService, "JwtTokenService must be initialised");
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(new AntPathRequestMatcher("/auth/register", HttpMethod.POST.name())).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/auth/login", HttpMethod.POST.name())).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/certificates/**", HttpMethod.GET.name())).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/tags/**", HttpMethod.GET.name())).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/generate/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/orders/**", HttpMethod.GET.name())).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/orders/**", HttpMethod.POST.name())).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/users/**", HttpMethod.GET.name())).authenticated()
                .requestMatchers(HttpMethod.GET, "/api/certificates/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**")).hasRole("ADMIN")
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}