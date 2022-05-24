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

        this.setAuthenticationManager(new ProviderManager(accessTokenAuthenticationProvider));
        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        SocialType socialType = extractSocialType(request);

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
        ResponseEntity<String> socialResponse = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", socialRequest , String.class );
        log.info("Social Response: {}", socialResponse);

        Map<String, String> result = new ObjectMapper().readValue(socialResponse.getBody(), Map.class);
        String accessToken = result.get("access_token");

        log.info("{}", socialType.getSocialName());
        log.info("accessToken: " + accessToken);
        return this.getAuthenticationManager().authenticate(new AccessTokenSocialTypeToken(accessToken, socialType));
    }

    private SocialType extractSocialType(HttpServletRequest request) {
        return Arrays.stream(SocialType.values())
                .filter(socialType ->
                        socialType.getSocialName()
                                .equals(request.getRequestURI().substring(DEFAULT_OAUTH2_LOGIN_REQUEST_URL_PREFIX.length())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 URL 주소입니다."));
    }
}
