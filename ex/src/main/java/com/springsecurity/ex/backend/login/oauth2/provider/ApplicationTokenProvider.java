package com.springsecurity.ex.backend.login.oauth2.provider;

import com.springsecurity.ex.backend.login.oauth2.authentication.ApplicationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTokenProvider {

    @Value("${app.auth.tokenExpiry}")
    private String expiry;
    private final Key key;
    private String socialId;

    public ApplicationTokenProvider(@Value("${app.auth.tokenSecret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public ApplicationToken createToken(String id, String expiry) {
        Date expiryDate = getExpiryDate(expiry);
        return new ApplicationToken(id, expiryDate, key);
    }

    public ApplicationToken createUserApplicationToken() {
        return createToken(socialId, expiry);
    }

    public static Date getExpiryDate(String expiry) {
//        System.out.println("System.currentTimeMillis() + Long.parseLong(expiry) = " + System.currentTimeMillis() + Long.parseLong(expiry));
        return new Date(System.currentTimeMillis() + Long.parseLong(expiry));
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }
}
