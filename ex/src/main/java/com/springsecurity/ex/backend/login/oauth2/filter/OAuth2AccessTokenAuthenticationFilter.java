package com.springsecurity.ex.backend.login.oauth2.filter;


import com.springsecurity.ex.backend.login.oauth2.SocialType;
import com.springsecurity.ex.backend.login.oauth2.authentication.AccessTokenSocialTypeToken;
import com.springsecurity.ex.backend.login.oauth2.provider.AccessTokenAuthenticationProvider;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

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

        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_NAME);
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
