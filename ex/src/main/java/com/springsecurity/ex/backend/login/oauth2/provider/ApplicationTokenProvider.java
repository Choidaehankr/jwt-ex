package com.springsecurity.ex.backend.login.oauth2.provider;

import com.springsecurity.ex.backend.login.oauth2.authentication.ApplicationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTokenProvider {

    @Value("${app.auth.tokenExpiry}")
    private String expiry;
    private final Key key;
    private String socialId;

    private static final String AUTHORITIES_KEY = "ROLE_";

    public ApplicationTokenProvider(@Value("${app.auth.tokenSecret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public ApplicationToken createToken(String id, String expiry) {
        Date expiryDate = getExpiryDate(expiry);
        return new ApplicationToken(id, expiryDate, key);
    }

    public ApplicationToken createUserApplicationToken(Long id) {
//        return createToken(socialId, expiry);
        return createToken(id.toString(), expiry);
    }

    public ApplicationToken convertApplicationToken(String token) {
        return new ApplicationToken(token, key);
    }

    public static Date getExpiryDate(String expiry) {
//        System.out.println("System.currentTimeMillis() + Long.parseLong(expiry) = " + System.currentTimeMillis() + Long.parseLong(expiry));
        return new Date(System.currentTimeMillis() + Long.parseLong(expiry));
    }

    public Authentication getAuthentication(ApplicationToken applicationToken) {

        if(applicationToken.validate()) {
            Claims claims = applicationToken.getTokenClaims();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[] {claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, applicationToken, authorities);
        } else {
            System.out.println("ApplicationTokenProvider-getAuthentication Error cause");
//            throw new TokenValiedException();
            applicationToken.getTokenClaims();
            throw new NullPointerException();
        }
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }
}
