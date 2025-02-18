package com.barogo.api.common.exception.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400(BAD_REQUEST)
    INVALID_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "요청 검증 실패했습니다."),
    USER_ID_DUPLICATED(HttpStatus.BAD_REQUEST, "아이디가 중복됩니다."),
    PASSWORD_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "비밀번호는 영어 대문자, 영어 소문자, 숫자, 특수문자 중 3종류 이상으로 12자리 이상의 문자열로 생성해야 합니다."),
    INVALID_DELIVERY_DATE(HttpStatus.BAD_REQUEST, "조회 가능한 기간은 최대 3일까지 가능합니다."),
    INVALID_DELIVERY_STATE(HttpStatus.BAD_REQUEST, "이 배달은 수정이 불가능한 상태입니다."),

    // 401(UNAUTHORIZED)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    INVALID_CREDENTIAL(HttpStatus.UNAUTHORIZED, "아이디가 존재하지 않거나 비밀번호가 틀렸습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),
    INVALID_USER(HttpStatus.UNAUTHORIZED, "인증 실패: 사용자를 찾을 수 없습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다."),

    // 404(NOT_FOUND)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 배달 주문을 찾을 수 없습니다."),

    // 500(INTERNAL_SERVER_ERROR)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    public int getStatusCode() {
        return status.value();
    }
}
