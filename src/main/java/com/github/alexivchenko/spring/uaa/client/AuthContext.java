package com.github.alexivchenko.spring.uaa.client;

/**
 * @author Alex Ivchenko
 */
public interface AuthContext<A> {
    A auth();
}
