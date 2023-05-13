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
                .requestMatchers(HttpMethod.GET, "/certificates/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tags/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                .requestMatchers(new AntPathRequestMatcher("/users/{id}/**", HttpMethod.GET.name())).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/users/**", HttpMethod.GET.name())).hasRole("MANAGER")
                .requestMatchers(new AntPathRequestMatcher("/orders/**")).hasRole("MANAGER")

                .requestMatchers(new AntPathRequestMatcher("/**")).hasRole("ADMIN")

                .requestMatchers(new AntPathRequestMatcher("/generate/**")).permitAll();
    }
}
