package com.springsecurity.ex.backend.login.oauth2.authentication;

import com.springsecurity.ex.backend.domain.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ApplicationToken {
    @Getter
    private final String token;
    private final Key key;

//    private static final String AUTHORITIES_KEY = "ROLE_USER";

    public ApplicationToken(String socialId, Date expiry, Key key) {
        this.key = key;
        this.token = createApplicationToken(socialId, expiry);
    }
    private String createApplicationToken(String socialId, Date expiry) {
        return Jwts.builder()
                .setSubject(socialId)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    public String getToken() {
        return this.token;
    }

    public Key getKey() {
        return this.key;
    }


}
