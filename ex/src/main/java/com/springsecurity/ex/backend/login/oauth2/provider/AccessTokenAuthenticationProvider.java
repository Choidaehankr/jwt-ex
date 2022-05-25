package com.springsecurity.ex.backend.login.oauth2.provider;


import com.springsecurity.ex.backend.domain.Member;
import com.springsecurity.ex.backend.domain.Role;
import com.springsecurity.ex.backend.domain.repository.MemberRepository;
import com.springsecurity.ex.backend.login.oauth2.authentication.AccessTokenSocialTypeToken;
import com.springsecurity.ex.backend.login.oauth2.authentication.ApplicationToken;
import com.springsecurity.ex.backend.login.oauth2.authentication.OAuth2UserDetails;
import com.springsecurity.ex.backend.login.oauth2.service.LoadUserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final LoadUserService loadUserService;
    private final MemberRepository memberRepository;

    private final ApplicationTokenProvider applicationTokenProvider;
    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        OAuth2UserDetails oAuth2User = loadUserService.getOAuth2UserDetails((AccessTokenSocialTypeToken) authentication);

        Member member = saveOrGet(oAuth2User);
        oAuth2User.setRoles(member.getRole().name());

        // 사용자 정보로 부터 받은 socialId를 통해 프로젝트 전용 토큰 생성
        saveApplicationToken(member.getSocialId());

        return AccessTokenSocialTypeToken.builder().principal(oAuth2User).authorities(oAuth2User.getAuthorities()).build();
    }

    private void saveApplicationToken(String socialId) {
        ApplicationToken applicationToken = applicationTokenProvider.createUserApplicationToken(socialId);
        System.out.println("applicationToken = " + applicationToken.getToken());
        // save Code ..

        // save Code
    }
    private Member saveOrGet(OAuth2UserDetails oAuth2User) {
        return memberRepository.findBySocialTypeAndSocialId(oAuth2User.getSocialType(),
                                                            oAuth2User.getSocialId())
                .orElseGet(()-> memberRepository.save(Member.builder()
                        .socialType(oAuth2User.getSocialType())
                        .socialId(oAuth2User.getSocialId())
//                        .role(Role.USER).build()));
                        .role(Role.GUEST).build()));
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenSocialTypeToken.class.isAssignableFrom(authentication);
    }

}
