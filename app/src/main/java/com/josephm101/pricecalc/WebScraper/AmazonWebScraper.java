package com.josephm101.pricecalc.WebScraper;

import android.os.StrictMode;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;


import java.io.IOException;
import java.util.regex.Pattern;

public class AmazonWebScraper {
    // User agent used to get the web page.
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36";

    // Spoof the scraping by telling the page from where the request has been sent:
    private static final String REFERRER = "https://www.duckduckgo.com";

    // Pattern to check if a string could be an url.
    private static final Pattern urlPattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    // Queries used to select the elements from the web page:
    private static final String priceQuery = ""
        + "span[id*=priceblock_ourprice],span[id*=priceblock_saleprice],"
        + "span.a-size-large.a-color-price.guild_priceblock_ourprice,"
        + "span.a-size-base.a-color-price.guild_priceblock_ourprice,"
        + "span.a-size-medium.a-color-price.guild_priceblock_ourprice,"
        + "span.a-size-large.a-color-price.guild_priceblock_saleprice,"
        + "span.a-size-base.a-color-price.guild_priceblock_saleprice,"
        + "span.a-size-medium.a-color-price.guild_priceblock_saleprice";
    private static final String dealPriceQuery = "span[id*=priceblock_dealprice]";
    private static final String currencyQuery = "span[id*=priceblock_currency_value]";
    private static final String titleQuery = "span[id=productTitle]";
    private static final String categoryQuery = "a.a-link-normal.a-color-tertiary";
    private static final String availabilityQuery = "span[id=availability],div[id=availability],p.a-spacing-micro.a-color-secondary.a-text-bold";

    // Default time to wait between each connection, in millis.
    private final static long DEFAULT_INTERVAL = 750;

    public static ProductInfo GetProductInfo(String productUrl) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Verify if productUrl is a valid URL.
        if (!urlPattern.matcher(productUrl).matches()) {
            return null;
        }
        Document document = Jsoup.connect(productUrl)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .get();

        if (document == null) {
            return null;
        }

        String name = "";
        double price = 0;

        // Get price

        //try {
        //    Elements elements2 = document.select(priceQuery);
        //    Log.i("LIB2", "PARSE: " + elements2.text());
        //
        //    if (elements2 != null && elements2.size() > 0) {
        //        Log.i("LIB", "GetProductInfo: " + elements2.get(0).text().substring(1));
        //        price = Double.parseDouble(elements2.get(0).text().substring(1));
        //    }
        //
        //    Elements elements = document.select(dealPriceQuery);
        //    if (elements != null && elements.size() > 0) {
        //        Log.i("LIB", "GetProductInfo: " + elements.get(0).text().substring(1));
        //        price = Double.parseDouble(elements.get(0).text().substring(1));
        //    }
        //} catch (Selector.SelectorParseException exception) {
        //    exception.printStackTrace();
        //}

        Elements priceElements = document.select(priceQuery);
        Log.d("WEB_SCRAPER", "PriceElements (Size): " + String.valueOf(priceElements.size()));
        if (priceElements.size() > 0) {
            String priceString = priceElements.get(0).text();
            priceString = priceString.replaceAll("[^0-9.]", "");
            price = Double.parseDouble(priceString);
        }
        else
        {
            // Try getting it a different way
            // Get "buy new" price
            Elements elements = document.getElementsByClass("a-color-price");
            Log.d("WEB_SCRAPER", "PriceElements (Size): " + String.valueOf(elements.size()));
            if (elements != null && elements.size() > 0) {
                for (int i = 0; i < elements.size(); i++) {
                    Log.d("WEB_SCRAPER", "Element " + i + ": " + elements.get(i).text());
                }
                String priceString = elements.get(0).text();
                priceString = priceString.replaceAll("[^0-9.]", "");
                price = Double.parseDouble(priceString);
            }


            // Elements elements = document.select("span.a-size-base.a-color-price");
            // Log.d("WEB_SCRAPER", "PriceElements (Size): " + String.valueOf(elements.size()));
            // if (elements.size() > 0) {
            //     for (int i = 0; i < elements.size(); i++) {
            //         Log.d("WEB_SCRAPER", "Element " + i + ": " + elements.get(i).text());
            //     }
            //     String priceString = elements.get(0).text();
            //     priceString = priceString.replaceAll("[^0-9.]", "");
            //     price = Double.parseDouble(priceString);
            // }
        }

        // Get name
        try {
            Elements elements = document.select(titleQuery);
            if (elements != null && elements.size() > 0) {
                name = elements.get(0).text();
            } else {
                name = "";
            }

        } catch (Selector.SelectorParseException exception) {
            exception.printStackTrace();
        }

        return new ProductInfo(name, price);
    }
}