package com.springsecurity.ex.backend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtResponseDto {
    @JsonProperty
    private final String jwt;
}
