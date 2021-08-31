package com.lucky.linkpal.utils

import java.util.regex.Pattern

class REGEX {
    companion object {
        val PASSWORD_PATTERN =
            Pattern.compile(
                "^" +
                        "(?=.*[0-9])" +
                        "(?=.*[a-zA-Z])" +
                        "(?=\\S+$)" +
                        ".{6,}" +
                        "$"
            )

        val PHONE_PATTERN1 = Pattern.compile("^(07|01).*[0-9]")
        val PHONE_PATTERN2 = Pattern.compile("^(\\+254).*[0-9]")

    }
}