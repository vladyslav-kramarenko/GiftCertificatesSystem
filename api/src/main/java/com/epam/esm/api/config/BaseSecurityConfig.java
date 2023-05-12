package com.epam.esm.api.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public abstract class BaseSecurityConfig {

    protected void configureCommon(HttpSecurity http) throws Exception {
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
                .requestMatchers(new AntPathRequestMatcher("/**")).hasRole("ADMIN");
    }
}
