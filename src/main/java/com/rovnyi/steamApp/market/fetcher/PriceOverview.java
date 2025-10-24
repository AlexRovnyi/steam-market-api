package com.rovnyi.steamApp.market.fetcher;

import java.util.Map;

/**
 * Entity representing price data of an item on the Steam Market.
 * <p>
 * This object is typically returned by {@link PriceOverviewFetcher} after parsing
 * the response from the <code>/priceoverview</code> API endpoint.
 */
public class PriceOverview {

    private boolean success;

    private double lowestPrice;

    private int volume;

    private double medianPrice;

    /**
     * Constructs a {@code PriceOverview} from a parsed JSON map.
     *
     * @param map The map representing the JSON response from Steam API
     */
    public PriceOverview(Map<String, Object> map) {
        this.success = (boolean) map.get("success");
        if (!success) return;

        this.lowestPrice = extractValueDouble((String) map.get("lowest_price"));

        if (map.size() == 2) {
            this.volume = 0;
            this.medianPrice = 0;
        } else {
            this.volume = extractValueInteger((String) map.get("volume"));
            this.medianPrice = extractValueDouble((String) map.get("median_price"));
        }
    }

    /**
     * Extracts a numeric value from a price string (removes currency symbols and formatting).
     *
     * @param value Raw string value (e.g. {@code "$0.20"})
     * @return Parsed double value
     */
    private double extractValueDouble(String value) {
        if (value == null) return 0;

        String clean = value.replaceAll("[^\\d.,]", "");

        clean = clean.replace(',', '.');

        if (clean.isEmpty()) return 0.0;

        return Double.parseDouble(clean);
    }

    private int extractValueInteger(String value) {
        return Integer.parseInt(value.replaceAll("[^\\d]", ""));
    }

    /**
     * Whether the API call returned a valid response.
     *
     * @return true if valid, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * The current lowest price of the item.
     *
     * @return Lowest listed price
     */
    public double getLowestPrice() {
        return lowestPrice;
    }

    /**
     * The number of items sold on the Steam Market in the last 24 hours.
     *
     * @return Sold quantity in the past 24 hours
     */
    public int getVolume() {
        return volume;
    }

    /**
     * The median price of all recent transactions.
     *
     * @return Median price value
     */
    public double getMedianPrice() {
        return medianPrice;
    }

    @Override
    public String toString() {
        return "PriceOverview{" +
               "success=" + success +
               ", lowest_price=" + lowestPrice +
               ", volume=" + volume +
               ", median_price=" + medianPrice +
               '}';
    }
}
