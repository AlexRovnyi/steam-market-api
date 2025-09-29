package com.rovnyi.steamApp.market.fetcher;

import com.rovnyi.steamApp.enums.AppID;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Fetches the icon URL of a Steam Market item using HTML parsing.
 * <p>
 * This class sends a request to the item's listing page and extracts
 * the image URL from the HTML content using {@link Jsoup}.
 */
public class ItemIconFetcher {

    private final OkHttpClient client = new OkHttpClient();

    private final AppID appID;

    private Logger log;

    /**
     * Constructs a new icon fetcher for a specific Steam application.
     *
     * @param appID the Steam App ID of the game (e.g. CS2)
     */
    public ItemIconFetcher(AppID appID) {
        this.appID = appID;
    }

    public ItemIconFetcher(AppID appID, Logger log) {
        this.appID = appID;
        this.log = log;
    }

    /**
     * Fetches the icon URL for the given {@code market_hash_name}.
     * <p>
     * The URL is extracted by parsing the item's listing page.
     *
     * @param marketHashName the unique market name of the item
     * @return the icon URL, or {@code null} if not found or request fails
     * @throws MarketFetcherException if a network or parsing error occurs
     */
    public String fetchIconUrl(String marketHashName) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("steamcommunity.com")
                .addPathSegment("market")
                .addPathSegment("listings")
                .addPathSegment(String.valueOf(appID.getID()))
                .addPathSegment(marketHashName)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            String html = response.body().string();

            Document document = Jsoup.parse(html);

            Element image = document.selectFirst("img[src*=/economy/image/]");

            if (image != null) {
                if (log != null) log.debug("Fetched icon url for: {}", marketHashName);
                return image.attr("src");
            }

            return null;
        } catch (IOException e) {
            if (log != null) log.error(e.getMessage());
            throw new MarketFetcherException(e);
        }
    }
}
