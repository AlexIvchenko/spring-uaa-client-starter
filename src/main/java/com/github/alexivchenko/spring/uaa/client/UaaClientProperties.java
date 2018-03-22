package com.github.alexivchenko.spring.uaa.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Alex Ivchenko
 */
@Getter
@Setter
@ConfigurationProperties("uaa.client")
public class UaaClientProperties {
    private Jwt jwt;
    private boolean enabled = false;
    private String id;
    private String secret;
    private List<String> scope;
    private String accessTokenUri;

    @Getter
    @Setter
    @ConfigurationProperties("jwt")
    public static class Jwt {
        private Key key;

        @Getter
        @Setter
        @ConfigurationProperties("public.key")
        public static class Key {
            public String uri = null;
            public String value = null;
        }
    }
}
