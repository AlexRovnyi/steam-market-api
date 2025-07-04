package com.rovnyi.steamApp.market.fetcher;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents an aggregated overview of a Steam Market item.
 * <p>
 * Combines data from multiple sources:
 * <ul>
 *     <li>{@link PriceOverview} — price, volume, median</li>
 *     <li>{@link ItemOrdersHistogram} — order graphs and highest buy order</li>
 *     <li>Icon URL — extracted via {@link ItemIconFetcher}</li>
 * </ul>
 * Also includes the {@code market_hash_name} and the time this overview was fetched.
 */
public class ItemOverview {

    private final String marketHashName;
    private final LocalDateTime fetchedAt;

    private final PriceOverview price;
    private final ItemOrdersHistogram orders;
    private final String iconUrl;

    /**
     * Constructs a new {@code ItemOverview} with full market data.
     *
     * @param price          Price data (lowest, median, volume)
     * @param orders         Buy/sell order data
     * @param marketHashName Unique item name in the Steam Market
     * @param fetchedAt      Time when this overview was fetched
     * @param iconUrl        URL to the item icon
     */
    public ItemOverview(PriceOverview price, ItemOrdersHistogram orders, String marketHashName, LocalDateTime fetchedAt, String iconUrl) {
        this.price = price;
        this.orders = orders;
        this.marketHashName = marketHashName;
        this.fetchedAt = fetchedAt;
        this.iconUrl = iconUrl;
    }

    /**
     * @return The item's market hash name (used in requests)
     */
    public String getMarketHashName() {
        return marketHashName;
    }

    /**
     * @return The timestamp when this data was retrieved
     */
    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    /**
     * @return The item's icon URL from the Steam Market
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * @return {@code true} if all fetchers succeeded and icon URL is available
     */
    public boolean isSuccess() {
        return price.isSuccess() && orders.isSuccess() && iconUrl != null;
    }

    /**
     * @return Lowest listed price for the item
     */
    public double getLowestPrice() {
        return price.getLowestPrice();
    }

    /**
     * @return Number of items sold in the last 24 hours
     */
    public int getVolume() {
        return price.getVolume();
    }

    /**
     * @return Median sale price of recent transactions
     */
    public double getMedianPrice() {
        return price.getMedianPrice();
    }

    /**
     * @return Highest current buy order for the item
     */
    public double getHighestBuyOrder() {
        return orders.getHighestBuyOrder();
    }

    /**
     * @return Buy order graph: price - order count
     */
    public Map<Double, Integer> getBuyOrderGraph() {
        return orders.getBuyOrderGraph();
    }

    /**
     * @return Sell order graph: price - order count
     */
    public Map<Double, Integer> getSellOrderGraph() {
        return orders.getSellOrderGraph();
    }

    @Override
    public String toString() {
        return "ItemOverview{" +
               "marketHashName='" + marketHashName + '\'' +
               ", fetchedAt=" + fetchedAt +
               ", price=" + price +
               ", orders=" + orders +
               ", iconUrl='" + iconUrl + '\'' +
               '}';
    }
}
