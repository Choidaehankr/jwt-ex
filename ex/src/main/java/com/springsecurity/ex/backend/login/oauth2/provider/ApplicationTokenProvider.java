package com.springsecurity.ex.backend.login.oauth2.provider;

import com.springsecurity.ex.backend.login.oauth2.authentication.ApplicationToken;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.sql.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTokenProvider {

    @Value("${app.auth.tokenExpiry}")
    private String expiry;
    private final Key key;

    public ApplicationTokenProvider(@Value("${app.auth.tokenSecret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public ApplicationToken createToken(String id, String expiry) {
        Date expiryDate = getExpiryDate(expiry);
        return new ApplicationToken(id, expiryDate, key);
    }

    public ApplicationToken createUserApplicationToken(String id) {
        return createToken(id, expiry);
    }

    public static Date getExpiryDate(String expiry) {
        return new Date(System.currentTimeMillis() + Long.parseLong(expiry));
    }
}
