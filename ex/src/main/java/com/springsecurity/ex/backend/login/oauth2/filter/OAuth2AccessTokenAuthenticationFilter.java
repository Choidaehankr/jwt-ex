package com.springsecurity.ex.backend.login.oauth2.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springsecurity.ex.backend.login.oauth2.SocialType;
import com.springsecurity.ex.backend.login.oauth2.authentication.AccessTokenSocialTypeToken;
import com.springsecurity.ex.backend.login.oauth2.provider.AccessTokenAuthenticationProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OAuth2AccessTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_OAUTH2_LOGIN_REQUEST_URL_PREFIX = "/oauth/login/";

    private static final String HTTP_METHOD = "GET";

    private static final String ACCESS_TOKEN_HEADER_NAME = "Authorization";

    private static final AntPathRequestMatcher DEFAULT_OAUTH2_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_OAUTH2_LOGIN_REQUEST_URL_PREFIX + "*", HTTP_METHOD);

    public OAuth2AccessTokenAuthenticationFilter (
            AccessTokenAuthenticationProvider accessTokenAuthenticationProvider,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler) {

        super(DEFAULT_OAUTH2_LOGIN_PATH_REQUEST_MATCHER);

        // AbstractAuthenticationProcessingFilter 를 커스텀하기 위해 ProviderManager 를 지정
        this.setAuthenticationManager(new ProviderManager(accessTokenAuthenticationProvider));
        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // AbstractAuthenticationProcessingFilter 의 추상 메서드를 구현. Authentication 객체를 반환
        SocialType socialType = extractSocialType(request);

        // 인가 코드 및 access_token 받기 (프론트에서 넘겨받을 예정)
        // start
        String authCode = request.getParameter("code");
        log.info("authCode: {}", authCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", "146414e0f2cf5ef05dee863aae51615a");
        map.add("code", authCode);
        map.add("redirect_uri", "http://localhost:8888/oauth/login/kakao");

        HttpEntity<MultiValueMap<String, String>> socialRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        // kauth.kakao.com/oauth/token 에 요청하고 헤더 정보를 포함한 (위의 map.add) 응답받기
        ResponseEntity<String> socialResponse = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", socialRequest , String.class );
        log.info("Social Response: {}", socialResponse);

        Map<String, String> result = new ObjectMapper().readValue(socialResponse.getBody(), Map.class);
        String accessToken = result.get("access_token");
        String refreshToken = result.get("refresh_token");

        log.info("{}", socialType.getSocialName());
        log.info("accessToken: " + accessToken);
        log.info("refreshToken: " + refreshToken);

        // end
        return this.getAuthenticationManager().authenticate(new AccessTokenSocialTypeToken(accessToken, socialType));
    }

    private SocialType extractSocialType(HttpServletRequest request) {
        return Arrays.stream(SocialType.values())
                .filter(socialType ->
                        socialType.getSocialName()
                                .equals(request.getRequestURI().substring(DEFAULT_OAUTH2_LOGIN_REQUEST_URL_PREFIX.length())))
                                // ~/kakao에서 kakao 추출
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 URL 주소입니다."));
    }
}
