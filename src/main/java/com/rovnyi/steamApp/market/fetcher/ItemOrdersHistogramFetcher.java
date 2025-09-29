package com.rovnyi.steamApp.market.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rovnyi.steamApp.enums.AppID;
import com.rovnyi.steamApp.enums.CountryCode;
import com.rovnyi.steamApp.enums.CurrencyCode;
import com.rovnyi.steamApp.enums.Language;
import com.rovnyi.steamApp.market.provider.ItemNameIdProvider;
import com.rovnyi.steamApp.market.provider.ResolvingIdProvider;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Fetches order book data for a specific Steam Market item.
 * <p>
 * This class sends a request to the <code>/itemordershistogram</code> endpoint and
 * parses the JSON response into an {@link ItemOrdersHistogram} object.
 */
public class ItemOrdersHistogramFetcher {

    private final ObjectMapper mapper = new ObjectMapper();

    private final OkHttpClient client = new OkHttpClient();

    private final Logger log;

    private final CountryCode country;

    private final Language language;

    private final CurrencyCode currency;

    private final ItemNameIdProvider provider;

    /**
     * Constructs a new {@code ItemOrdersHistogramFetcher} with the specified configuration.
     *
     * @param country  Country code to include in the request
     * @param language Language code to include in the request
     * @param currency Currency in which to return price values
     * @param provider ID provider to resolve the {@code item_nameid}
     */
    public ItemOrdersHistogramFetcher(CountryCode country, Language language, CurrencyCode currency, ItemNameIdProvider provider, Logger log) {
        this.country = country;
        this.language = language;
        this.currency = currency;
        this.provider = provider;
        this.log = log;
    }

    /**
     * Calls the Steam Market API to fetch order histogram data for the given item.
     *
     * @param marketHashName The unique market hash name of the item
     * @return An {@link ItemOrdersHistogram} object containing buy/sell order information,
     *         or {@code null} if the response is invalid or incomplete
     * @throws MarketFetcherException If a network or parsing error occurs
     */
    public ItemOrdersHistogram callAPI(String marketHashName) {
        String itemNameId = provider.get(marketHashName);
        if (itemNameId == null) {if (log != null) log.info("\"{}\" - itemNameId is null", marketHashName);}

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("steamcommunity.com")
                .addPathSegment("market")
                .addPathSegment("itemordershistogram")
                .addQueryParameter("country", country.getCode())
                .addQueryParameter("language", language.getLanguage())
                .addQueryParameter("currency", String.valueOf(currency.getCode()))
                .addQueryParameter("item_nameid", itemNameId)
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

            return new ItemOrdersHistogram(map);
        } catch (IOException e) {
            throw new MarketFetcherException(e);
        }
    }

    /**
     * Builder class for {@link ItemOrdersHistogramFetcher}.
     * <p>
     * Provides default configuration:
     * <ul>
     *   <li>AppID = CS2</li>
     *   <li>Currency = USD</li>
     *   <li>Country = ENGLISH</li>
     *   <li>Language = ENGLISH</li>
     *   <li>ItemNameIdProvider = ResolvingIdProvider</li>
     * </ul>
     */
    public static class Builder {
        private CountryCode country = CountryCode.ENGLISH;
        private Language language = Language.ENGLISH;
        private CurrencyCode currency = CurrencyCode.USD;
        private AppID appID = AppID.COUNTER_STRIKE_2;
        private ItemNameIdProvider provider;
        private Logger log = null;

        /**
         * Sets the country code for the request.
         *
         * @param country Country code
         * @return The current builder instance
         */
        public Builder country(CountryCode country) {
            this.country = country;
            return this;
        }

        /**
         * Sets the language code for the request.
         *
         * @param language Language
         * @return The current builder instance
         */
        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        /**
         * Sets the currency code for the request.
         *
         * @param currency Currency
         * @return The current builder instance
         */
        public Builder currency(CurrencyCode currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Sets the Steam App ID (used by default provider).
         *
         * @param appID Steam App ID
         * @return The current builder instance
         */
        public Builder appID(AppID appID) {
            this.appID = appID;
            return this;
        }

        /**
         * Sets a custom {@link ItemNameIdProvider}.
         *
         * @param provider The provider to resolve {@code item_nameid}
         * @return The current builder instance
         */
        public Builder provider(ItemNameIdProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder withLogger(Logger log) {
            this.log = log;
            return this;
        }

        /**
         * Builds a configured {@link ItemOrdersHistogramFetcher} instance.
         *
         * @return A new {@link ItemOrdersHistogramFetcher}
         */
        public @NotNull ItemOrdersHistogramFetcher build() {
            if (provider == null) {
                ResolvingIdProvider resolvingIdProvider = new ResolvingIdProvider(appID);
                resolvingIdProvider.setLogger(log);
                provider = resolvingIdProvider;
            }
            return new ItemOrdersHistogramFetcher(country, language, currency, provider, log);
        }
    }
}