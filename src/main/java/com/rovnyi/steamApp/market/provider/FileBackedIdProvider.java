package com.rovnyi.steamApp.market.provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A persistent file-backed implementation of {@link ItemNameIdProvider}.
 * <p>
 * Loads item_nameid mappings from a JSON file on initialization and writes updates back to it.
 * Designed for long-term storage and thread-safe concurrent access.
 */
public class FileBackedIdProvider implements ItemNameIdProvider {

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final Map<String, String> itemNameIdMap = new ConcurrentHashMap<>();

    private final Path itemNameIdFile;

    /**
     * Constructs a new file-backed provider that includes the received data file.
     *
     * @param itemNameIdFile The file that contains the list of item_nameid.
     */
    public FileBackedIdProvider(Path itemNameIdFile) {
        this.itemNameIdFile = itemNameIdFile;

        try {
            itemNameIdMap.putAll(mapper.readValue(itemNameIdFile.toFile(), new TypeReference<>() {}));
        } catch (IOException e) {
            throw new IdStorageException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String marketHashName) {
        return itemNameIdMap.get(marketHashName);
    }

    /**
     * Returns a shallow copy of the internal cache as a {@link HashMap}.
     * <p>
     * Useful for inspection or external serialization.
     *
     * @return Map with all cached item_nameid entries.
     */
    public Map<String, String> getMap() {
        return new HashMap<>(itemNameIdMap);
    }

    /**
     * Adds the given item_nameid to the internal map and persists it.
     * If the mapping already exists, it will not be overwritten.
     *
     * @param marketHashName Unique item name
     * @param itemNameId     ID to associate
     */
    public synchronized void put(String marketHashName, String itemNameId) {
        if (itemNameIdMap.putIfAbsent(marketHashName, itemNameId) == null) {
            saveToFile(itemNameIdFile);
        }
    }

    /**
     * Same as {@link #put(String, String)}, but saves to the provided file instead of the default one.
     *
     * @param itemNameIdFile Target file
     */
    public synchronized void put(String marketHashName, String itemNameId, Path itemNameIdFile) {
        if (itemNameIdMap.putIfAbsent(marketHashName, itemNameId) == null) {
            saveToFile(itemNameIdFile);
        }
    }

    /**
     * Adds all entries from the given map to the cache-file.
     *
     * @param itemNameIdMap Map of item names and their corresponding IDs
     */
    public synchronized void putAll(Map<String, String> itemNameIdMap) {
        this.itemNameIdMap.putAll(itemNameIdMap);

        saveToFile(itemNameIdFile);
    }

    /**
     * Adds all entries from the given map to the given cache-file.
     *
     * @param itemNameIdMap Map of item names and their corresponding IDs
     * @param itemNameIdFile File for saving
     */
    public synchronized void putAll(Map<String, String> itemNameIdMap, Path itemNameIdFile) {
        this.itemNameIdMap.putAll(itemNameIdMap);

        saveToFile(itemNameIdFile);
    }

    /**
     * Checks if the item_nameid exists in the cache-file.
     *
     * @param marketHashName Item name to check
     * @return true if the name exists, false otherwise
     */
    public boolean contains(String marketHashName) {
        return itemNameIdMap.containsKey(marketHashName);
    }

    /**
     * Removes the given item_nameid from the current cache-file.
     *
     * @param marketHashName Item name to remove
     * @return true if the entry was removed, false if it didn't exist
     */
    public synchronized boolean remove(String marketHashName) {
        if (itemNameIdMap.containsKey(marketHashName)) {
            itemNameIdMap.remove(marketHashName);

            saveToFile(itemNameIdFile);

            return true;
        }

        return false;
    }

    /**
     * Removes the given item_nameid from the given cache-file.
     *
     * @param marketHashName Item name to remove
     * @param itemNameIdFile Given file
     * @return true if the entry was removed, false if it didn't exist
     */
    public synchronized boolean remove(String marketHashName, Path itemNameIdFile) {
        if (itemNameIdMap.containsKey(marketHashName)) {
            itemNameIdMap.remove(marketHashName);

            saveToFile(itemNameIdFile);

            return true;
        }

        return false;
    }

    private synchronized void saveToFile(Path itemNameIdFile) {
        try {
            mapper.writeValue(itemNameIdFile.toFile(), this.itemNameIdMap);
        } catch (IOException e) {
            throw new IdStorageException(e);
        }
    }
}