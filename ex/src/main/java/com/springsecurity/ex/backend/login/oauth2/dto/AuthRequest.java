package com.springsecurity.ex.backend.login.oauth2.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String accessToken;
}
