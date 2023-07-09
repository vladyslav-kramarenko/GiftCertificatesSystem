package com.epam.esm.api.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public abstract class BaseSecurityConfig {

    protected void configureCommon(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/auth0/**")).permitAll()
                .requestMatchers(HttpMethod.GET, "/certificates/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/certificates/search/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tags/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tags/popular").permitAll()
                .requestMatchers(HttpMethod.GET, "/image/**").permitAll()

                .requestMatchers(HttpMethod.POST, "/orders").authenticated()
                .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/validate-token").authenticated()
                .requestMatchers(new AntPathRequestMatcher("/users/{id}/**", HttpMethod.GET.name())).authenticated()

                .requestMatchers(HttpMethod.POST, "/image/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/certificates/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/certificates/**").hasRole("ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/**")).hasRole("ADMIN")

                .requestMatchers(new AntPathRequestMatcher("/generate/**")).permitAll();
    }
}
