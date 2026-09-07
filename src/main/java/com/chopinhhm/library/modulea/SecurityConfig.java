package com.chopinhhm.library.modulea;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .headers().frameOptions().sameOrigin()
            .and().authorizeRequests()
            .antMatchers("/", "/index.html", "/app.js", "/styles.css", "/favicon.ico", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
            .antMatchers(HttpMethod.POST, "/api/circulation/borrow", "/api/circulation/loans/*/return").hasRole("ADMIN")
            .antMatchers("/api/circulation/admin/**").hasRole("ADMIN")
            .antMatchers("/api/admin/**").hasRole("ADMIN")
            .antMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
            .and().httpBasic();
    }
}
