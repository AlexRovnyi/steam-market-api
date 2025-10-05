package com.rovnyi.steamApp.market.fetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rovnyi.steamApp.enums.AppID;
import com.rovnyi.steamApp.enums.CurrencyCode;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarketSearchFetcher {

    private final ObjectMapper mapper = new ObjectMapper();

    private final OkHttpClient client = new OkHttpClient();

    private final Logger log;

    private final AppID appID;

    public MarketSearchFetcher(AppID appID,Logger log) {
        this.appID = appID;
        this.log = log;
    }

    public List<String> callAPI(String marketHashName, int start, int count) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("steamcommunity.com")
                .addPathSegment("market")
                .addPathSegment("search")
                .addPathSegment("render")
                .addQueryParameter("query", marketHashName)
                .addQueryParameter("start", String.valueOf(start))
                .addQueryParameter("count", String.valueOf(count))
                .addQueryParameter("appid", String.valueOf(appID.getID()))
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

            return extractNamesFromJson(json);
        } catch (IOException e) {
            if (log != null) log.error(e.getMessage());
            throw new MarketFetcherException(e);
        }
    }

    public List<String> extractNamesFromJson(String json) throws IOException {
        JsonNode root = mapper.readTree(json);

        String html = root.path("results_html").asText();
        List<String> names = new ArrayList<>();

        if (html != null && !html.isEmpty()) {
            Document doc = Jsoup.parse(html);
            for (Element span : doc.select("span.market_listing_item_name")) {
                String name = span.text().trim();
                if (!name.isEmpty()) {
                    names.add(name);
                }
            }
        }

        return names;
    }

    public static class Builder {
        private AppID appID = AppID.COUNTER_STRIKE_2;
        private Logger log = null;

        public Builder appID(AppID appID) {
            this.appID = appID;
            return this;
        }

        public Builder withLogger(Logger log) {
            this.log = log;
            return this;
        }

        public @NotNull MarketSearchFetcher build() {
            return new MarketSearchFetcher(appID, log);
        }
    }
}
