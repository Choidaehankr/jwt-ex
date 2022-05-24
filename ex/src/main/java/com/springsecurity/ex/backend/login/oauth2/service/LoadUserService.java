package com.springsecurity.ex.backend.login.oauth2.service;


import com.springsecurity.ex.backend.login.oauth2.SocialType;
import com.springsecurity.ex.backend.login.oauth2.authentication.AccessTokenSocialTypeToken;
import com.springsecurity.ex.backend.login.oauth2.authentication.OAuth2UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoadUserService {

    private final RestTemplate restTemplate = new RestTemplate();

    private SocialLoadStrategy socialLoadStrategy;

    public OAuth2UserDetails getOAuth2UserDetails(AccessTokenSocialTypeToken authentication) {

        SocialType socialType = authentication.getSocialType();

        setSocialLoadStrategy(socialType);

        String socialPk = socialLoadStrategy.getSocialPk(authentication.getAccessToken());

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
