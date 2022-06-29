package io.blog.springblogapp.security;

import io.blog.springblogapp.SpringApplicationContext;
import io.blog.springblogapp.utils.AppProperties;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final long EMAIL_EXPIRATION_TIME = 86400000; // 1 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/v1/users";
    public static final String LOGIN_URL = "/api/v1/auth/login";
    public static final String VERIFICATION_EMAIL_URL = "/api/v1/users/email-verification";
    public static final String RESET_PASSWORD_URL = "/api/v1/users/reset-password-request";
    public static final String RESET_PASSWORD_UPDATE_URL = "/api/v1/users/reset-password";

    public static String getSecretToken() {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");

        return appProperties.getSecretToken();
    }

}
