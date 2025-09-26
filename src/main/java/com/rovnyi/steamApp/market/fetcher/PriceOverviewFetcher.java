package com.rovnyi.steamApp.market.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rovnyi.steamApp.enums.AppID;
import com.rovnyi.steamApp.enums.CurrencyCode;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

/**
 * Fetches pricing information for a specific Steam Market item.
 * <p>
 * This class sends a request to the <code>/priceoverview</code> endpoint and
 * parses the JSON response into a {@link PriceOverview} object.
 */
public class PriceOverviewFetcher {

    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient();

    private final AppID appID;
    private final CurrencyCode currency;

    /**
     * Constructs a new {@code PriceOverviewFetcher} with the specified app ID and currency.
     *
     * @param appID    Steam App ID (e.g. CS2)
     * @param currency Currency in which to return price values
     */
    public PriceOverviewFetcher(AppID appID, CurrencyCode currency) {
        this.appID = appID;
        this.currency = currency;
    }

    /**
     * Calls the Steam Market API to fetch price overview data for the given item.
     *
     * @param marketHashName The unique market hash name of the item
     * @return A {@link PriceOverview} object containing the parsed pricing information,
     *         or {@code null} if the response is invalid or incomplete
     * @throws MarketFetcherException If a network or parsing error occurs
     */
    public PriceOverview callAPI(String marketHashName) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("steamcommunity.com")
                .addPathSegment("market")
                .addPathSegment("priceoverview")
                .addQueryParameter("appid", String.valueOf(appID.getID()))
                .addQueryParameter("market_hash_name", marketHashName)
                .addQueryParameter("currency", String.valueOf(currency.getCode()))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            String json = response.body().string();

            Map<String, Object> map = mapper.readValue(json, Map.class);
            if (map.size() < 2) return null;

            return new PriceOverview(map);
        } catch (IOException e) {
            throw new MarketFetcherException(e);
        }
    }

    /**
     * Builder class for {@link PriceOverviewFetcher}.
     * <p>
     * Provides default configuration: AppID = CS2, Currency = USD.
     */
    public static class Builder {
        private AppID appID = AppID.COUNTER_STRIKE_2;
        private CurrencyCode currency = CurrencyCode.USD;

        /**
         * Sets the Steam App ID.
         *
         * @param appID Steam App ID
         * @return The current builder instance
         */
        public Builder appID(AppID appID) {
            this.appID = appID;
            return this;
        }

        /**
         * Sets the currency code for the request.
         *
         * @param currency Currency code
         * @return The current builder instance
         */
        public Builder currency(CurrencyCode currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Builds a configured {@link PriceOverviewFetcher} instance.
         *
         * @return A new {@link PriceOverviewFetcher}
         */
        public @NotNull PriceOverviewFetcher build() {
            return new PriceOverviewFetcher(appID, currency);
        }
    }
}
