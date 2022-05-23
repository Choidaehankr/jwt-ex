package com.springsecurity.ex.backend.login.oauth2.filter.handler;

import com.springsecurity.ex.backend.domain.Role;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("로그인 성공!: " + authentication.getPrincipal());

        if(authentication.getAuthorities().stream().anyMatch(s -> s.getAuthority().equals(Role.GUEST.getGrantedAuthority()))) {
            System.out.println("가입되지 않은 유저입니다. 회원가입으로 이동합니다.");
            System.out.println("authentication.getName() = " + authentication.getName());
            System.out.println("authentication.getAuthorities() = " + authentication.getAuthorities());
            System.out.println("authentication.getDetails() = " + authentication.getDetails());
            System.out.println("authentication.getClass() = " + authentication.getClass());
            System.out.println("authentication = " + authentication.getCredentials());
//            response.sendRedirect("/singUp");
            return;
        }
        System.out.println("회원가입이 된 사용자입니다.");
    }
}
