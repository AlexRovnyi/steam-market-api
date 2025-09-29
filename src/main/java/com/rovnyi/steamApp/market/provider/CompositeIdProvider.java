package com.rovnyi.steamApp.market.provider;

import org.slf4j.Logger;

import java.util.Map;

/**
 * A composite implementation of {@link ItemNameIdProvider} that first attempts to retrieve the item_nameid from a file-based cache,
 * and if not found, falls back to a network-based resolver.
 * <p>
 * Resolved entries are automatically cached for future lookups. The file-backed storage allows persistent caching between sessions.
 */
public class CompositeIdProvider implements ItemNameIdProvider {

    private final ResolvingIdProvider resolvingIdProvider;

    private final FileBackedIdProvider fileBackedIdProvider;

    private Logger log;

    /**
     * Constructs a new composite provider with both a resolver and a file-based cache.
     *
     * @param resolvingIdProvider  The provider used for resolving item_nameid from the Steam Market page
     * @param fileBackedIdProvider The provider used for caching and loading item_nameid from file
     */
    public CompositeIdProvider (ResolvingIdProvider resolvingIdProvider, FileBackedIdProvider fileBackedIdProvider) {
        this.resolvingIdProvider = resolvingIdProvider;
        resolvingIdProvider.setLogger(log);

        this.fileBackedIdProvider = fileBackedIdProvider;
        fileBackedIdProvider.setLogger(log);
    }

    /**
     * {@inheritDoc}
     * <p>
     * First checks the file-backed provider. If not found, attempts resolution via the resolver and caches the result.
     */
    @Override
    public String get(String marketHashName) {
        String itemNameId = fileBackedIdProvider.get(marketHashName);
        if (itemNameId != null) {
            if (log != null) log.debug("Fetched itemNameId from fileBackedIdProvider for market hash: {}", marketHashName);
            return itemNameId;
        }

        itemNameId = resolvingIdProvider.get(marketHashName);
        if (log != null) log.debug("Fetched itemNameId from resolvingIdProvider for market hash: {}", marketHashName);


        if (itemNameId == null) return null;

        fileBackedIdProvider.put(marketHashName, itemNameId);
        if (log != null) log.debug("Cached itemNameId for \"{}\" ({}) to file: {}", marketHashName, itemNameId, fileBackedIdProvider.getItemNameIdFile());

        return itemNameId;
    }

    /**
     * Returns a snapshot copy of the internal file-backed cache.
     *
     * @return Map with all cached item_nameid entries
     */
    public Map<String, String> getMap() {
        return fileBackedIdProvider.getMap();
    }

    /**
     * Checks whether the item_nameid is already cached.
     *
     * @param marketHashName The market_hash_name to check
     * @return true if found in the file cache, false otherwise
     */
    public boolean contains(String marketHashName) {
        return fileBackedIdProvider.contains(marketHashName);
    }

    /**
     * Removes the item_nameid mapping from the file-backed cache.
     *
     * @param marketHashName The market_hash_name to remove
     * @return true if removed, false if it wasn't found
     */
    public boolean remove(String marketHashName) {
        return fileBackedIdProvider.remove(marketHashName);
    }

    public void setLogger(Logger log) {
        this.log = log;
    }
}