package com.github.alexivchenko.spring.uaa.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexivchenko.spring.uaa.client.json.JacksonAdapter;
import com.github.alexivchenko.spring.uaa.client.json.JsonConverter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Map;

/**
 * @author Alex Ivchenko
 */
@Slf4j
public class DefaultAuthContext<A> implements AuthContext<A> {
    private final Class<A> type;
    private final JsonConverter<A> converter;
    private final String payloadLocationClaim;

    public static <A> TypeStageBuilder<A> builder() {
        return new Builder<>();
    }

    private DefaultAuthContext(Class<A> type, JsonConverter<A> converter, String payloadLocationClaim) {
        this.type = type;
        if (converter == null) {
            this.converter = new JacksonAdapter<>(new ObjectMapper());
        } else {
            this.converter = converter;
        }
        this.payloadLocationClaim = payloadLocationClaim;
    }

    @Override
    public A auth() {
        OAuth2Authentication authentication = authentication();
        Map<String, Object> extra = getExtraInfo(authentication);
        String json;
        if (payloadLocationClaim != null) {
            json = converter.toJson(extra.get(payloadLocationClaim));
        } else {
            json = converter.toJson(extra);
        }
        return converter.toObject(json, type);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getExtraInfo(OAuth2Authentication authentication) {
        OAuth2AuthenticationDetails oauthDetails
                = (OAuth2AuthenticationDetails) authentication.getDetails();
        return (Map<String, Object>) oauthDetails
                .getDecodedDetails();
    }

    private OAuth2Authentication authentication() {
        return (OAuth2Authentication) SecurityContextHolder.getContext()
                .getAuthentication();
    }

    private static class Builder<T> implements TypeStageBuilder<T>, AggregatorStageBuilder<T> {
        private Class<T> authType;
        private JsonConverter<T> converter;
        private String payloadLocationClaim;

        @Override
        public AggregatorStageBuilder<T> type(@NonNull Class<T> authType) {
            this.authType = authType;
            return this;
        }

        @Override
        public AggregatorStageBuilder<T> mapper(JsonConverter<T> converter) {
            this.converter = converter;
            return this;
        }

        @Override
        public AggregatorStageBuilder<T> startsFrom(String payloadLocationClaim) {
            this.payloadLocationClaim = payloadLocationClaim;
            return this;
        }

        @Override
        public AuthContext<T> build() {
            return new DefaultAuthContext<>(authType, converter, payloadLocationClaim);
        }
    }

    public interface TypeStageBuilder<T> {
        AggregatorStageBuilder<T> type(Class<T> authType);
    }

    public interface AggregatorStageBuilder<T> extends MapperStageBuilder<T>, PayloadLocationClaimStageBuilder<T>, FinalStageBuilder<T> {

    }

    public interface MapperStageBuilder<T> {
        AggregatorStageBuilder<T> mapper(JsonConverter<T> converter);
    }

    public interface PayloadLocationClaimStageBuilder<T> {
        AggregatorStageBuilder<T> startsFrom(String payloadLocationClaim);
    }

    public interface FinalStageBuilder<A> {
        AuthContext<A> build();
    }
}
