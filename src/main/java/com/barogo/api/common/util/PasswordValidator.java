package com.barogo.api.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class PasswordValidator {
    private static final Pattern UPPER_CASE = Pattern.compile("[A-Z]");     // 대문자
    private static final Pattern LOWER_CASE = Pattern.compile("[a-z]");     // 소문자
    private static final Pattern DIGIT = Pattern.compile("[0-9]");          // 숫자
    private static final Pattern SPECIAL_CHAR = Pattern.compile("[\\W_]");  // 특수문자

    public static boolean isValid(String password) {
        // 비밀번호 길이 검증
        if (StringUtils.isBlank(password) || password.length() < 12) {
            return false;
        }

        // 3종류 이상으로 이루어졌는지 검증
        int count = 0;
        if (UPPER_CASE.matcher(password).find()) count++;
        if (LOWER_CASE.matcher(password).find()) count++;
        if (DIGIT.matcher(password).find()) count++;
        if (SPECIAL_CHAR.matcher(password).find()) count++;

        return count >= 3;
    }
}
