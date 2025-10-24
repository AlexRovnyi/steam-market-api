package com.rovnyi.steamApp.market.fetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents buy/sell order data for a Steam Market item.
 * <p>
 * Parsed from the JSON response returned by the {@code /itemordershistogram} endpoint.
 */
public class ItemOrdersHistogram {

    private Logger log;

    private boolean success;

    private double highestBuyOrder;

    private Map<Double, Integer> buyOrderGraph;

    private Map<Double, Integer> sellOrderGraph;

    /**
     * Constructs an {@code ItemOrdersHistogram} from the raw JSON response map.
     *
     * @param map Parsed JSON data as a Map
     */
    public ItemOrdersHistogram(Map<String, Object> map) {
        this(map, null);
    }

    public ItemOrdersHistogram(Map<String, Object> map, Logger log) {
        this.success = (Integer) map.get("success") == 1;
        if (!success) return;

        this.highestBuyOrder = extractValueDouble((String) map.get("highest_buy_order"));
        this.buyOrderGraph = new LinkedHashMap<>();
        this.sellOrderGraph = new LinkedHashMap<>();

        extractOrderGraph(map, "buy_order_graph", buyOrderGraph);
        extractOrderGraph(map, "sell_order_graph", sellOrderGraph);

        this.log = log;
    }

    /**
     * Parses the order graph data (buy/sell) and populates the corresponding map.
     *
     * @param map            The full response map
     * @param orderGraphType Either {@code "buy_order_graph"} or {@code "sell_order_graph"}
     * @param orderGraph     Target map to populate
     */
    private void extractOrderGraph(Map<String, Object> map, String orderGraphType, Map<Double, Integer> orderGraph) {
        Object graph = map.get(orderGraphType);

        if (graph instanceof List<?> list) {
            int prev = 0;

            for (Object o : list) {
                if (o instanceof List<?> entry && entry.size() > 1) {
                    double price = Double.parseDouble(entry.get(0).toString());
                    int cumulative = Integer.parseInt(entry.get(1).toString());
                    int actual = cumulative - prev;

                    prev = cumulative;

                    orderGraph.put(price, actual);
                }
            }
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

        try {
            return Double.parseDouble(value) / 100;
        }  catch (NumberFormatException e) {
            if (log != null) log.info("value is not a number");
        }

        String clean = value.replaceAll("[^\\d.,]", "");

        clean = clean.replace(',', '.');

        if (clean.isEmpty()) return 0.0;

        return Double.parseDouble(clean);
    }

    /**
     * @return {@code true} if the request was successful and data is valid
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return Highest buy order currently on the market
     */
    public double getHighestBuyOrder() {
        return highestBuyOrder;
    }

    /**
     * @return A map where keys are prices and values are the number of buy orders at each level
     */
    public Map<Double, Integer> getBuyOrderGraph() {
        return buyOrderGraph;
    }

    /**
     * @return A map where keys are prices and values are the number of sell orders at each level
     */
    public Map<Double, Integer> getSellOrderGraph() {
        return sellOrderGraph;
    }

    @Override
    public String toString() {
        return "ItemOrdersHistogram{" +
               "success=" + success +
               ", highestBuyOrder=" + highestBuyOrder +
               ", buyOrderGraph=" + buyOrderGraph +
               ", sellOrderGraph=" + sellOrderGraph +
               '}';
    }
}
