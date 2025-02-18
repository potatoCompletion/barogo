package com.barogo.api.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    @DisplayName("비밀번호 검증 메서드 테스트")
    void 비밀번호_검증_메서드_테스트() {
        /// 비밀번호 조건: 비밀번호는 영어 대문자, 영어 소문자, 숫자, 특수문자 중 3종류 이상으로 12자리 이상의 문자열로 생성해야 합니다

        // given
        String[] invalidPasswords = {
                "Short1!",  // 길이 제한
                "Abcdefghijklmnop", "ABCDEFGH!!!!!", "12345678abcd" // 3종류 미만
        };

        // expected
        Arrays.stream(invalidPasswords)
                .forEach(password -> assertFalse(PasswordValidator.isValid(password), "실패한 비밀번호: " + password));
    }
}