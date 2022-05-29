package com.springsecurity.ex.backend.login.oauth2.service;


import com.springsecurity.ex.backend.login.oauth2.SocialType;
import com.springsecurity.ex.backend.login.oauth2.authentication.AccessTokenSocialTypeToken;
import com.springsecurity.ex.backend.login.oauth2.authentication.ApplicationToken;
import com.springsecurity.ex.backend.login.oauth2.authentication.OAuth2UserDetails;
import com.springsecurity.ex.backend.login.oauth2.dto.AuthResponse;
import com.springsecurity.ex.backend.login.oauth2.provider.ApplicationTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoadUserService {

    private final RestTemplate restTemplate = new RestTemplate();

    private SocialLoadStrategy socialLoadStrategy;

    private final ApplicationTokenProvider applicationTokenProvider;

//    private ApplicationToken app;

    public OAuth2UserDetails getOAuth2UserDetails(AccessTokenSocialTypeToken authentication) {

        SocialType socialType = authentication.getSocialType();

        setSocialLoadStrategy(socialType);

        String socialPk = socialLoadStrategy.getSocialPk(authentication.getAccessToken());

        // applicationToken 발급 위치 변경
//        ApplicationToken applicationToken = applicationTokenProvider.createUserApplicationToken(socialPk);
//
//        this.app = applicationToken;
//
//        System.out.println("@@@ applicationToken = " + applicationToken.getToken());

        // socialId와 socialType 을 반환
        return OAuth2UserDetails.builder()
                .socialId(socialPk)
                .socialType(socialType)
                .build();
    }

    private void setSocialLoadStrategy(SocialType socialType) {
//        this.socialLoadStrategy = switch (socialType) {
//                    case KAKAO -> new KaKaoLoadStrategy();
//                    default -> throw new IllegalArgumentException("지원하지 않는 로그인 형식입니다.");
//                }
        // 임의 수정 : switch 지원  안함
        if (socialType.getSocialName() == "kakao") {
            this.socialLoadStrategy = new KaKaoLoadStrategy();
        } else {
            throw new IllegalArgumentException("지원하지 않는 로그인 형식입니다.");
        }
    }
}
