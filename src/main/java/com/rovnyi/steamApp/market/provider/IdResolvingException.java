package com.rovnyi.steamApp.market.provider;

/**
 * Exception thrown when resolving {@code item_nameid} from the Steam Market fails.
 * <p>
 * Typically used by {@link ResolvingIdProvider} when a network request fails or
 * the expected data cannot be extracted from the HTML response.
 */
public class IdResolvingException extends RuntimeException {

    /**
     * Constructs a new {@code IdResolvingException} with the specified detail message.
     *
     * @param message the detail message describing the cause
     */
    public IdResolvingException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code IdResolvingException} with the specified cause.
     *
     * @param cause the underlying {@link Throwable} that caused this exception
     */
    public IdResolvingException(Throwable cause) {
        super(cause);
    }
}
