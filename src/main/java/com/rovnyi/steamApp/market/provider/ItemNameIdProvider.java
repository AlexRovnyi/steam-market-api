package com.rovnyi.steamApp.market.provider;

/**
 * Interface for item_nameid providers that provides an object identifier given its market_hash_name.
 * <p>
 * Implementations may use a cache (in memory or on disk), network parsing, or a combination of both.
 */
public interface ItemNameIdProvider {


    /**
     * Returns the item_nameid corresponding to the passed market_hash_name.
     *
     * @param marketHashName Unique name of the item in the Steam Market
     * @return item_nameid as a string, or null if the value is not found
     */
    String get(String marketHashName);
}