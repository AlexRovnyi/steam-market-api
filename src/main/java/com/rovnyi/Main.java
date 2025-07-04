package com.rovnyi;

import com.rovnyi.steamApp.market.client.SteamMarketClient;
import com.rovnyi.steamApp.market.enums.AppID;
import com.rovnyi.steamApp.market.enums.CountryCode;
import com.rovnyi.steamApp.market.enums.CurrencyCode;
import com.rovnyi.steamApp.market.enums.Language;
import com.rovnyi.steamApp.market.fetcher.ItemOrdersHistogramFetcher;
import com.rovnyi.steamApp.market.fetcher.ItemOverview;
import com.rovnyi.steamApp.market.fetcher.PriceOverviewFetcher;
import com.rovnyi.steamApp.market.provider.CompositeIdProvider;
import com.rovnyi.steamApp.market.provider.FileBackedIdProvider;
import com.rovnyi.steamApp.market.provider.ItemNameIdProvider;
import com.rovnyi.steamApp.market.provider.ResolvingIdProvider;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        long l = System.currentTimeMillis();
        
        Path file = Path.of("src", "main", "resources", "test.json");

        SteamMarketClient client = new SteamMarketClient.Builder()
                .appID(AppID.COUNTER_STRIKE_2)
                .country(CountryCode.UKRAINIAN)
                .currency(CurrencyCode.UAH)
                .language(Language.UKRAINIAN)
                .provider(new CompositeIdProvider(new ResolvingIdProvider(AppID.COUNTER_STRIKE_2), new FileBackedIdProvider(file)))
                .build();

        ItemOverview itemOverview = client.fetchOverview("Souvenir SSG 08 | Prey (Field-Tested)");

        long l1 = System.currentTimeMillis();

        System.out.println(itemOverview);
        System.out.println(l1 - l);
    }

    private static void testItemOrdersHistogramFetcher() {
        long l = System.currentTimeMillis();

        Path file = Path.of("src", "main", "resources", "test.json");

        var fetcher = new ItemOrdersHistogramFetcher.Builder()
                .appID(AppID.COUNTER_STRIKE_2)
                .currency(CurrencyCode.UAH)
                .language(Language.UKRAINIAN)
                .country(CountryCode.UKRAINIAN)
                .provider(new CompositeIdProvider(new ResolvingIdProvider(AppID.COUNTER_STRIKE_2), new FileBackedIdProvider(file)))
                .build();

        var itemOrdersHistogram = fetcher.callAPI("Souvenir SSG 08 | Prey (Field-Tested)");

        long l1 = System.currentTimeMillis();

        System.out.println(itemOrdersHistogram);
        System.out.println(l1 - l);
    }

    private static void testPriceOverviewFetcher() {
        long l = System.currentTimeMillis();

        var fetcher = new PriceOverviewFetcher.Builder()
                .appID(AppID.COUNTER_STRIKE_2)
                .currency(CurrencyCode.UAH)
                .build();

        var priceOverview = fetcher.callAPI("Souvenir SSG 08 | Prey (Field-Tested)");

        long l1 = System.currentTimeMillis();

        System.out.println(priceOverview);
        System.out.println(l1 - l);
    }

    private static void testProvider() {
        long l = System.currentTimeMillis();

        Path file = Path.of("src", "main", "resources", "test.json");

        ItemNameIdProvider itemNameIdProvider = new CompositeIdProvider(new ResolvingIdProvider(AppID.COUNTER_STRIKE_2), new FileBackedIdProvider(file));

        String s = itemNameIdProvider.get("Souvenir SSG 08 | Prey (Field-Tested)");

        long l1 = System.currentTimeMillis();

        System.out.println(s + " : " + (l1 - l));
    }
}

