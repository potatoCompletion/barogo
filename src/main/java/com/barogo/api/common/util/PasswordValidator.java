package com.barogo.api.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class PasswordValidator {
    private static final Pattern UPPER_CASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWER_CASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR = Pattern.compile("[\\W_]");

    public static boolean isValid(String password) {
        if (StringUtils.isBlank(password) || password.length() < 12) {
            return false;
        }

        int count = 0;
        if (UPPER_CASE.matcher(password).find()) count++;
        if (LOWER_CASE.matcher(password).find()) count++;
        if (DIGIT.matcher(password).find()) count++;
        if (SPECIAL_CHAR.matcher(password).find()) count++;

        return count >= 3;
    }
}
