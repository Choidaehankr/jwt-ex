package com.springsecurity.ex.backend.login.oauth2.filter;

import com.springsecurity.ex.backend.login.oauth2.authentication.ApplicationToken;
import com.springsecurity.ex.backend.login.oauth2.authentication.JwtHeaderUtil;
import com.springsecurity.ex.backend.login.oauth2.provider.ApplicationTokenProvider;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ApplicationTokenProvider applicationTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String tokenStr = JwtHeaderUtil.getAccessToken(request);
            ApplicationToken token = applicationTokenProvider.convertApplicationToken(tokenStr);

            if(token.validate()) {  // 토큰 유효성 검사
                Authentication authentication = applicationTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        }
    }

}
