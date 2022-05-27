package com.springsecurity.ex.backend.login.oauth2.handler;

import com.springsecurity.ex.backend.domain.Role;
import com.springsecurity.ex.backend.login.oauth2.authentication.ApplicationToken;
import com.springsecurity.ex.backend.login.oauth2.authentication.OAuth2UserDetails;
import com.springsecurity.ex.backend.login.oauth2.provider.ApplicationTokenProvider;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

// 1. 로그인 성공 시 어떤 URL로 Redirect 할 지 결정
// 2. 로그인 실패 에러 세션 지우기
// 3. 로그인 성공 시 실패 카운터 초기화
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ApplicationTokenProvider applicationTokenProvider;

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

        System.out.println("회원가입이 된 사용자입니다. 토큰을 발급합니다.");

        // 로그인 성공시 handler 에서 토큰 발급하기.. applicationToken 발급 받을 때, 그냥 socialId 주지 말자...
//        saveApplicationToken((OAuth2UserDetails)authentication.); // 사용자 정보로 부터 받은 socialId를 통해 프로젝트 전용 토큰 생성
    }

    private void saveApplicationToken(String socialId) {
        ApplicationToken applicationToken = applicationTokenProvider.createUserApplicationToken(socialId);
        System.out.println("applicationToken = " + applicationToken.getToken());
        // save Code ..

        // save Code
    }
}
