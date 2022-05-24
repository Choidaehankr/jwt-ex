package com.springsecurity.ex.backend.login.oauth2.service;


import com.springsecurity.ex.backend.login.oauth2.SocialType;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

@Slf4j
public class KaKaoLoadStrategy extends SocialLoadStrategy{

    protected String sendRequestToSocialSite(HttpEntity request) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(SocialType.KAKAO.getUserInfoUrl(),
                    SocialType.KAKAO.getMethod(),
                    request,
                    RESPONSE_TYPE);
            return response.getBody().get("id").toString();
        } catch (Exception e) {
            log.error("KAKAO 사용자 정보를 받아오던 중 에러가 발생했습니다. {}", e.getMessage());
            throw e;
        }
    }
}
