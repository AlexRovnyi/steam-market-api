package com.rovnyi.steamApp.client;

import com.rovnyi.steamApp.enums.AppID;
import com.rovnyi.steamApp.enums.CountryCode;
import com.rovnyi.steamApp.enums.CurrencyCode;
import com.rovnyi.steamApp.enums.Language;
import com.rovnyi.steamApp.market.fetcher.ItemOverview;
import com.rovnyi.steamApp.market.fetcher.ItemOverviewService;
import com.rovnyi.steamApp.market.provider.ItemNameIdProvider;
import com.rovnyi.steamApp.market.provider.ResolvingIdProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade client for interacting with the Steam Market API.
 * <p>
 * Provides a simplified interface for fetching item data (pricing, order stats, and icon).
 * Internally uses {@link ItemOverviewService} to combine multiple fetchers.
 * <p>
 * Recommended for applications that want an easy-to-use entry point with sane defaults.
 */
public class SteamMarketClient {

    private final ItemOverviewService itemOverviewService;

    private final Logger log;

    /**
     * Constructs a new {@code SteamMarketClient} using the provided configuration.
     *
     * @param currency Desired currency for price data
     * @param appID    Steam App ID (e.g., CS2)
     * @param country  Country code used for localization
     * @param language Interface language for Steam
     * @param provider Provider for resolving item_nameid
     */
    public SteamMarketClient(CurrencyCode currency, AppID appID, CountryCode country, Language language, ItemNameIdProvider provider, Logger log) {
        this.itemOverviewService = new ItemOverviewService.Builder()
                .currency(currency)
                .appID(appID)
                .country(country)
                .language(language)
                .provider(provider)
                .withLogger(log)
                .build();

        this.log = log;
    }

    /**
     * Fetches full item data (price overview, order histogram, and icon).
     *
     * @param marketHashName The unique market_hash_name of the item
     * @return {@link ItemOverview} object containing all available information, or {@code null} if any request failed
     */
    public ItemOverview fetchOverview(String marketHashName) {
        if (log != null) log.info("SteamMarketClient is Fetching overview for {}", marketHashName);
        return itemOverviewService.callAPI(marketHashName);
    }

    /**
     * Builder class for {@link SteamMarketClient}.
     * <p>
     * Provides default configuration: USD, ENGLISH, CS2.
     */
    public static class Builder {
        private CountryCode country = CountryCode.ENGLISH;
        private Language language = Language.ENGLISH;
        private CurrencyCode currency = CurrencyCode.USD;
        private AppID appID = AppID.COUNTER_STRIKE_2;
        private ItemNameIdProvider provider;
        private Logger log = null;

        /**
         * Sets the country code used for request localization.
         *
         * @param country ISO country code (e.g., US, UA)
         * @return this builder
         */
        public Builder country(CountryCode country) {
            this.country = country;
            return this;
        }

        /**
         * Sets the Steam interface language for responses.
         *
         * @param language Language to use
         * @return this builder
         */
        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        /**
         * Sets the desired currency for price values.
         *
         * @param currency Currency code
         * @return this builder
         */
        public Builder currency(CurrencyCode currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Sets the Steam App ID to target (e.g., CS2, Dota2).
         *
         * @param appID Steam App ID
         * @return this builder
         */
        public Builder appID(AppID appID) {
            this.appID = appID;
            return this;
        }

        /**
         * Sets the provider used for resolving item_nameid.
         * If not set, defaults to {@link ResolvingIdProvider}.
         *
         * @param provider Implementation of {@link ItemNameIdProvider}
         * @return this builder
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
         * Builds and returns a configured {@link SteamMarketClient} instance.
         *
         * @return a ready-to-use {@code SteamMarketClient}
         */
        public @NotNull SteamMarketClient build() {
            if (provider == null) {
                provider = new ResolvingIdProvider(appID);
            }
            return new SteamMarketClient(currency, appID, country, language, provider, log);
        }
    }
}