package com.rovnyi.steamApp.market.fetcher;

import com.rovnyi.steamApp.enums.AppID;
import com.rovnyi.steamApp.enums.CountryCode;
import com.rovnyi.steamApp.enums.CurrencyCode;
import com.rovnyi.steamApp.enums.Language;
import com.rovnyi.steamApp.market.provider.ItemNameIdProvider;
import com.rovnyi.steamApp.market.provider.ResolvingIdProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.time.LocalDateTime;

/**
 * Aggregates item market data from multiple sources into a single {@link ItemOverview} object.
 * <p>
 * Combines:
 * <ul>
 *     <li>{@link PriceOverviewFetcher} — fetches lowest price, median price, volume</li>
 *     <li>{@link ItemOrdersHistogramFetcher} — fetches buy/sell order graphs and highest buy order</li>
 *     <li>{@link ItemIconFetcher} — fetches item icon URL</li>
 * </ul>
 * Uses builder pattern for flexible configuration. If no {@link ItemNameIdProvider} is provided, defaults to {@link ResolvingIdProvider}.
 */
public class ItemOverviewService {

    private final PriceOverviewFetcher priceFetcher;

    private final ItemOrdersHistogramFetcher ordersFetcher;

    private final ItemIconFetcher iconFetcher;

    private Boolean iconRequired;

    private final Logger log;

    /**
     * Constructs a new {@code ItemOverviewService} with the given configuration.
     *
     * @param currency Currency to use when fetching prices
     * @param appID    Steam App ID for the game (e.g., CS2)
     * @param country  Country code (affects localization)
     * @param language Language code (affects localization)
     * @param provider Provider for resolving item_nameid
     */
    public ItemOverviewService(CurrencyCode currency, AppID appID, CountryCode country, Language language, ItemNameIdProvider provider, boolean iconRequired, Logger log) {
        this.priceFetcher = new PriceOverviewFetcher.Builder()
                .appID(appID)
                .currency(currency)
                .withLogger(log)
                .build();

        this.ordersFetcher = new ItemOrdersHistogramFetcher.Builder()
                .country(country)
                .language(language)
                .currency(currency)
                .appID(appID)
                .provider(provider)
                .withLogger(log)
                .build();

        this.iconFetcher = new ItemIconFetcher(appID);
        this.iconRequired = iconRequired;
        this.log = log;
    }

    /**
     * Aggregates all available market data for the given item.
     *
     * @param marketHashName The item's unique name in the Steam Market
     * @return {@link ItemOverview} object with merged price, order, and icon data, or {@code null} if any fetch fails
     */
    public ItemOverview callAPI(String marketHashName) {
        PriceOverview price = priceFetcher.callAPI(marketHashName);
        if (log != null) log.debug("ItemOverviewService fetched PriceOverview for \"{}\": {}", marketHashName, price);

        ItemOrdersHistogram orders = ordersFetcher.callAPI(marketHashName);
        if (log != null) log.debug("ItemOverviewService fetched ItemOrdersHistogram for \"{}\": {}", marketHashName, orders);

        String iconUrl = iconFetcher.fetchIconUrl(marketHashName);
        if (log != null) log.debug("ItemOverviewService fetched IconUrl for \"{}\": {}", marketHashName, iconUrl);

        if (price == null) {
            if (log != null) log.info("\"{}\" - PriceOverview is null", marketHashName);
            return null;
        }
        else if (orders == null) {
            if (log != null) log.info("\"{}\" - ItemOrdersHistogram is null", marketHashName);
            return null;
        }
        else if (iconUrl == null && iconRequired) {
            if (log != null) log.info("\"{}\" - IconUrl is null", marketHashName);
            return null;
        }

        LocalDateTime fetchedAt = LocalDateTime.now();

        return new ItemOverview(price, orders, marketHashName, fetchedAt, iconUrl);
    }

    /**
     * Builder for {@link ItemOverviewService}.
     * <p>
     * Provides default values:
     * <ul>
     *     <li>Currency = USD</li>
     *     <li>AppID = CS2</li>
     *     <li>Country = ENGLISH</li>
     *     <li>Language = ENGLISH</li>
     * </ul>
     */
    public static class Builder {
        private CountryCode country = CountryCode.ENGLISH;
        private Language language = Language.ENGLISH;
        private CurrencyCode currency = CurrencyCode.USD;
        private AppID appID = AppID.COUNTER_STRIKE_2;
        private ItemNameIdProvider provider;
        private boolean iconRequired = false;
        private  Logger log = null;

        /**
         * Sets the country code for localization.
         *
         * @param country Country code (e.g., "EN")
         * @return This builder instance
         */
        public Builder country(CountryCode country) {
            this.country = country;
            return this;
        }

        /**
         * Sets the language for localization.
         *
         * @param language Language enum
         * @return This builder instance
         */
        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        /**
         * Sets the desired currency.
         *
         * @param currency Currency enum
         * @return This builder instance
         */
        public Builder currency(CurrencyCode currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Sets the App ID of the game.
         *
         * @param appID App ID
         * @return This builder instance
         */
        public Builder appID(AppID appID) {
            this.appID = appID;
            return this;
        }

        /**
         * Sets a custom {@link ItemNameIdProvider}.
         *
         * @param provider Provider implementation
         * @return This builder instance
         */
        public Builder provider(ItemNameIdProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder setIconRequired(boolean iconRequired) {
            this.iconRequired = iconRequired;
            return this;
        }

        public Builder withLogger(Logger log) {
            this.log = log;
            return this;
        }

        /**
         * Builds the configured {@link ItemOverviewService}.
         * If no provider is set, defaults to {@link ResolvingIdProvider}.
         *
         * @return New {@link ItemOverviewService} instance
         */
        public @NotNull ItemOverviewService build() {
            if (provider == null) {
                ResolvingIdProvider resolvingIdProvider = new ResolvingIdProvider(appID);
                resolvingIdProvider.setLogger(log);
                provider = resolvingIdProvider;
            }
            return new ItemOverviewService(currency, appID, country, language, provider, iconRequired, log);
        }
    }

    public void setIconRequired(Boolean iconRequired) {
        this.iconRequired = iconRequired;
    }
}
