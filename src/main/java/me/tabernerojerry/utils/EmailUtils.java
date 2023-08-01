package me.tabernerojerry.utils;

import java.text.MessageFormat;

public class EmailUtils {

    public static String getEmailMessage(String name, String host, String token) {
        return MessageFormat.format("Hello {0}, \n\n Your new account has been created. Please verify your account. \n\n {1} \n\n The Support Team", name, getVerificationUrl(host, token));
    }

    public static String getVerificationUrl(String host, String token) {
        return MessageFormat.format("{0}/api/users?token={1}", host, token);
    }

}
