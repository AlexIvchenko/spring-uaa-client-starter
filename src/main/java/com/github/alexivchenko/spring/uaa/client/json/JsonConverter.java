package com.github.alexivchenko.spring.uaa.client.json;

/**
 * @author Alex Ivchenko
 */
public interface JsonConverter<T> {
    T toObject(String json, Class<T> type);

    String toJson(Object object);
}
