package com.rovnyi.steamApp.market.provider;

/**
 * Exception thrown when reading from or writing to the item_nameid storage file fails.
 * <p>
 * Typically used by file-backed providers like {@link FileBackedIdProvider} to indicate
 * I/O issues during persistence operations.
 */
public class IdStorageException extends RuntimeException {

    /**
     * Constructs a new {@code IdStorageException} with the specified detail message.
     *
     * @param message the detail message describing the cause
     */
    public IdStorageException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code IdStorageException} with the specified cause.
     *
     * @param cause the underlying {@link Throwable} that caused this exception
     */
    public IdStorageException(Throwable cause) {
        super(cause);
    }
}
