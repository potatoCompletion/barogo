package com.barogo.api.auth.response;

import lombok.Builder;

public record TokenResponse(String accessToken) {

    @Builder
    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
