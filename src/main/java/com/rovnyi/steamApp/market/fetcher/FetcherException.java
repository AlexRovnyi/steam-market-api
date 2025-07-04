package com.rovnyi.steamApp.market.fetcher;

/**
 * Exception thrown when an error occurs while fetching data from the Steam Market.
 * <p>
 * Used to wrap lower-level {@link java.io.IOException} or JSON parsing failures
 * during API requests (e.g. in {@link PriceOverviewFetcher}, {@link ItemOrdersHistogramFetcher}).
 */
public class FetcherException extends RuntimeException {

    /**
     * Constructs a new {@code FetcherException} with the specified detail message.
     *
     * @param message the detail message
     */
    public FetcherException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code FetcherException} with the specified cause.
     *
     * @param cause the underlying cause of the error (e.g. IOException)
     */
    public FetcherException(Throwable cause) {
        super(cause);
    }
}
