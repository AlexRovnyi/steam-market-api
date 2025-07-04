package com.rovnyi.steamApp.market.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory implementation of {@link ItemNameIdProvider} that stores item_nameid mappings in a thread-safe {@link ConcurrentHashMap}.
 * <p>
 * This provider does not persist data between sessions and is useful for short-lived or test environments.
 */
public class InMemoryIdProvider implements ItemNameIdProvider {

    private final Map<String, String> itemNameIdMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new in-memory provider with the given initial data.
     *
     * @param itemNameIdMap A map containing initial marketHashName - item_nameid mappings.
     */
    public InMemoryIdProvider(Map<String, String> itemNameIdMap) {
        this.itemNameIdMap.putAll(itemNameIdMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String marketHashName) {
        return itemNameIdMap.get(marketHashName);
    }

    /**
     * Returns a snapshot copy of the internal cache as a {@link HashMap}.
     *
     * @return Map with all cached item_nameid entries.
     */
    public Map<String, String> getMap() {
        return new HashMap<>(itemNameIdMap);
    }

    /**
     * Adds the given item_nameid to the cache if it doesn't already exist.
     *
     * @param marketHashName Unique item name
     * @param itemNameId     ID to be associated with the given name
     */
    public void put(String marketHashName, String itemNameId) {
        itemNameIdMap.putIfAbsent(marketHashName, itemNameId);
    }

    /**
     * Adds all entries from the given map to the internal cache.
     *
     * @param itemNameIdMap Map of item names and their corresponding IDs
     */
    public void putAll(Map<String, String> itemNameIdMap) {
        this.itemNameIdMap.putAll(itemNameIdMap);
    }

    /**
     * Checks if the item_nameid exists in the cache.
     *
     * @param marketHashName Item name to check
     * @return true if the name exists, false otherwise
     */
    public boolean contains(String marketHashName) {
        return itemNameIdMap.containsKey(marketHashName);
    }

    /**
     * Removes the given item_nameid from the cache.
     *
     * @param marketHashName Item name to remove
     * @return true if the entry was removed, false if it didn't exist
     */
    public boolean remove(String marketHashName) {
        return itemNameIdMap.remove(marketHashName) != null;
    }
}