package com.rovnyi.steamApp.market.provider;

import com.rovnyi.steamApp.enums.AppID;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A network-based implementation of {@link ItemNameIdProvider} that retrieves the item_nameid
 * by parsing the item's Steam Market listing page.
 * <p>
 * This provider performs a direct HTTP request and extracts the item_nameid using a regular expression
 * on the HTML response.
 * <p>
 * Use this only if the item_nameid is not already cached, as it makes a full network call per request.
 */
public class ResolvingIdProvider implements ItemNameIdProvider {

    private final OkHttpClient client = new OkHttpClient();

    private final AppID appID;

    private Logger log;

    /**
     * Constructs a new {@code ResolvingIdProvider} for the specified Steam application.
     *
     * @param appID The application ID (e.g. CS2) used in the item URL path
     */
    public ResolvingIdProvider(AppID appID) {
        this.appID = appID;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Makes an HTTP GET request to the item's listing page and extracts the item_nameid using a regex pattern.
     *
     * @param marketHashName The market_hash_name of the item
     * @return The resolved item_nameid, or {@code null} if not found
     * @throws IdResolvingException If an I/O error occurs during the request
     */
    @Override
    public String get(String marketHashName) {
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
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/126.0.6478.185 Safari/537.36")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            String html = response.body().string();

            Pattern pattern = Pattern.compile("Market_LoadOrderSpread\\(\\s*(\\d+)\\s*\\)");

            Matcher matcher = pattern.matcher(html);

            if (matcher.find()) {
                if (log != null) log.debug("ResolvingIdProvider fetched itemNameId for marketHashName: {}", marketHashName);
                return matcher.group(1);
            }

            if (log != null) log.debug("ResolvingIdProvider failed for marketHashName: {}", marketHashName);
            return null;
        } catch (IOException e) {
            if (log != null) log.error(e.getMessage());
            throw new IdResolvingException(e);
        }
    }

    public void setLogger(Logger log) {
        this.log = log;
    }
}