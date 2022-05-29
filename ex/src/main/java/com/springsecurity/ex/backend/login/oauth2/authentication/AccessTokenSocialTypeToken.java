package com.springsecurity.ex.backend.login.oauth2.authentication;

import com.springsecurity.ex.backend.login.oauth2.SocialType;
import java.util.Collection;
import lombok.Builder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AccessTokenSocialTypeToken extends AbstractAuthenticationToken {

    private Object principal;  // OAuth2UserDetails 타입

    private String accessToken;
    private SocialType socialType;

    private String applicationToken;

    public AccessTokenSocialTypeToken(String accessToken, SocialType socialType) {
        super(null);
        this.accessToken = accessToken;
        this.socialType = socialType;
        setAuthenticated(false);
    }

    @Builder
//    public AccessTokenSocialTypeToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
    public AccessTokenSocialTypeToken(Object principal, Collection<? extends GrantedAuthority> authorities, String applicationToken) {
        super(authorities);
        this.principal = principal;
        this.applicationToken = applicationToken;
        super.setAuthenticated(true);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public SocialType getSocialType() {
        return socialType;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

}
