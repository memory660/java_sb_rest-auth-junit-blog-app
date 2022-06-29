package io.blog.springblogapp.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class Utils {

    private final Random RANDOM = new SecureRandom();

    public String generateUserId() {
        return generateRandomString();
    }

    public String generateAddressId() {
        return generateRandomString();
    }

    private String generateRandomString() {
        String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(40);

        for (int i = 0; i < 40; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(sb);
    }

}
