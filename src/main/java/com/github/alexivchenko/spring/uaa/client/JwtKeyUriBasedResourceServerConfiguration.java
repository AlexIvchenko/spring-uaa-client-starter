package com.github.alexivchenko.spring.uaa.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Alex Ivchenko
 */
@Slf4j
@Configuration
@EnableResourceServer
@ConditionalOnProperty("uaa.client.enabled")
@EnableConfigurationProperties(UaaClientProperties.class)
public class JwtKeyUriBasedResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    private final UaaClientProperties properties;

    @Autowired
    public JwtKeyUriBasedResourceServerConfiguration(UaaClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        String publicKey;
        String publicKeyUri = properties.getJwt().getKey().getUri();
        if (publicKeyUri != null) {
            log.info("public key uri: " + publicKeyUri);
            publicKey = (String) new RestTemplate().getForObject(publicKeyUri, Map.class).get("value");
        } else {
            publicKey = properties.getJwt().getKey().getValue();
        }
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        log.info("public key: " + publicKey);
        converter.setAccessTokenConverter(customAccessTokenConverter());
        converter.setVerifierKey(publicKey);
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Bean
    public CustomAccessTokenConverter customAccessTokenConverter() {
        return new CustomAccessTokenConverter();
    }
}
