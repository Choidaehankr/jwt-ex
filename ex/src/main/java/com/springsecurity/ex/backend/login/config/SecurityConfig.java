package com.springsecurity.ex.backend.login.config;


import com.springsecurity.ex.backend.login.oauth2.filter.OAuth2AccessTokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2AccessTokenAuthenticationFilter oAuth2AccessTokenAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//                .antMatchers("/oauth/login/*").permitAll()
//                .antMatchers("/").permitAll();
//                .anyRequest().hasRole("USER");
        httpSecurity.addFilterBefore(oAuth2AccessTokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);
    }
}
