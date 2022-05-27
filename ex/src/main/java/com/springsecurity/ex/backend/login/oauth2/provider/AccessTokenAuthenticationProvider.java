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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {
//  AuthenticationProvider 를 구현받아 authenticate 와 supports 를 구현해야 함.
    private final LoadUserService loadUserService;  // restTemplate 를 통해서 AccessToken 을 가지고 회원 정보를 가져오는 역할
    private final MemberRepository memberRepository;  // 받아온 정보를 통해 DB에서 회원을 조회하는 역할

    private final ApplicationTokenProvider applicationTokenProvider;

    private String applicationToken_tmp;

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        OAuth2UserDetails oAuth2User = loadUserService.getOAuth2UserDetails((AccessTokenSocialTypeToken) authentication);
//        OAuth2UserDetails 는 UserDetails 를 상속받아 구현한 클래스. 이후 일반 회원가입 시 UserDetails 를 사용하는 부분과의 다형성을 위해 이처럼 구현
//        getOAuth2UserDetails 에서는 restTemplate 와 AccessToken 을 가지고 회원 정보를 조회한다.
        Member member = saveOrGet(oAuth2User);  // 식별자와 소셜 로그인 방식을 통해 회원을 DB 에서 조회 후 없다면 추가. 있다면 그대로 반환
        oAuth2User.setRoles(member.getRole().name());  // Role 의 name 은 ADMIN, USER, GUEST 로 ROLE_ 을 붙여주는 과정이 필요. setRoles 가 담당.

        // 현재 socialId를 ApplicationTokenProvider 에 저장. (jwt 발급을 위해)
        applicationTokenProvider.setSocialId(member.getSocialId());

        return AccessTokenSocialTypeToken.builder().principal(oAuth2User).authorities(oAuth2User.getAuthorities()).build();
        // AccessTokenSocialTypeToken 객체를 반환. principal 은 OAuth2UserDetails 객체
        // UserDetails 타입으로 회원의 정보를 어디서든 조회 가능
    }

    private Member saveOrGet(OAuth2UserDetails oAuth2User) {
        return memberRepository.findBySocialTypeAndSocialId(oAuth2User.getSocialType(),
                                                            oAuth2User.getSocialId())
                .orElseGet(()-> memberRepository.save(Member.builder()
                        .socialType(oAuth2User.getSocialType())
                        .socialId(oAuth2User.getSocialId())
                        .role(Role.USER).build()));
//                        .role(Role.GUEST).build()));  // GUEST로 설정..
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenSocialTypeToken.class.isAssignableFrom(authentication);
        // AccessTokenSocialTypeToken 타입의 authentication 객체이면 해당 Provider 가 처리한다.
    }

}