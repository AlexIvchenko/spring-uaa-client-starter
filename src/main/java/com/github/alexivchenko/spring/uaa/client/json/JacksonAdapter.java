package com.github.alexivchenko.spring.uaa.client.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author Alex Ivchenko
 */
public class JacksonAdapter<A> implements JsonConverter<A> {
    private final ObjectMapper mapper;

    public JacksonAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public A toObject(String json, Class<A> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
