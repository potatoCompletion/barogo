package com.barogo.api.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application.properties에 저장된 secretKey, expirationTime을 사용하기 위한 클래스
 * <p>
 * 실제 업무 환경이라면 환경 변수, 클라우드 서비스 등의 방법으로 더 안전하게 관리할 수 있습니다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long expiration;
}