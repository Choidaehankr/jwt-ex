package com.springsecurity.ex.backend.login.config;


import com.springsecurity.ex.backend.login.oauth2.filter.JwtAuthenticationFilter;
import com.springsecurity.ex.backend.login.oauth2.filter.OAuth2AccessTokenAuthenticationFilter;
import com.springsecurity.ex.backend.login.oauth2.provider.ApplicationTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2AccessTokenAuthenticationFilter oAuth2AccessTokenAuthenticationFilter;

    private final ApplicationTokenProvider applicationTokenProvider;

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers("/swagger*/**");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(applicationTokenProvider);

        httpSecurity.authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(oAuth2AccessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//                .antMatchers("/oauth/login/*").permitAll()
//                .antMatchers("/").permitAll();
//                .anyRequest().hasRole("USER");
    }
}
