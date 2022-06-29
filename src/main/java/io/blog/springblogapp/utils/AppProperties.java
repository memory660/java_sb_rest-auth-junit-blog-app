package io.blog.springblogapp.utils;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AppProperties {

    private Environment env;

    public String getSecretToken() {
        return env.getProperty("secretToken");
    }

}
