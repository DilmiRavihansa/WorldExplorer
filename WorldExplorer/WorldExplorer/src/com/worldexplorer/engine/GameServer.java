package com.worldexplorer.engine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import com.worldexplorer.net.HttpSupport;

/**
 * Talks to the external REST Countries API and builds questions from the
 * country data. The country list is downloaded once and cached, so the free
 * plan (1,000 requests per month) is not used up.
 *
 * The key comes from config.properties (api.key) or the RESTCOUNTRIES_API_KEY environment variable.
 */
public class GameServer implements QuestionProvider {

    public static final String MODE_COUNTRY = "Guess the Country";
    public static final String MODE_CAPITAL = "Guess the Capital";
    public static final String MODE_FLAG = "Identify the Flag";

    private static final String DEFAULT_BASE = "https://api.restcountries.com/countries/v5";
    private static final String FIELDS = "names.common,capitals,codes.alpha_2";
    private static final String FLAG_CDN = "https://flags.restcountries.com/v5/w320/";

    /** Simple country record built from the API response. */
    public static class Country {
        public String name;
        public String capital;
        public String code;
    }

    private static List<Country> cache = null;
    private final Random rnd = new Random();

    private static String lastError = "";

    /** API base URL from config.properties (api.base.url), or the default. */
    private static String baseUrl() {
        return Config.get("api.base.url", DEFAULT_BASE);
    }

    /** Looks for api.key in config.properties, then the RESTCOUNTRIES_API_KEY environment variable. */
    public static String apiKey() {
        String k = Config.get("api.key", null);
        if (k != null && !k.equals("YOUR_API_KEY_HERE")) return k;
        k = System.getenv("RESTCOUNTRIES_API_KEY");
        if (k != null && !k.isBlank()) return k.trim();
        return null;
    }

    @Override
    public boolean isConfigured() { return apiKey() != null; }

    /** Saves the key into config.properties so the player only has to enter it once. */
    @Override
    public void configure(String key) {
        Config.set("api.key", key.trim());
        cache = null;
    }

    @Override
    public String getLastError() { return lastError; }

    /** Downloads all countries (100 per request on the free plan = 3 requests). */
    @SuppressWarnings("unchecked")
    private static synchronized List<Country> loadCountries() {
        if (cache != null) return cache;
        lastError = "";
        List<Country> list = new ArrayList<>();
        String key = apiKey();
        if (key == null) {
            lastError = "No API key found. Put it in config.properties (api.key=...). Looked at: " + Config.describe() + "";
            return list;
        }
        try {
            int offset = 0;
            boolean more = true;
            int pages = 0;
            boolean slim = true; // first try with response_fields, fall back to omitting translations
            while (more && pages < 10) {
                String params = slim ? "response_fields=" + FIELDS : "response_fields_omit=names.translations";
                String url = baseUrl() + "?" + params + "&limit=100&offset=" + offset;
                HttpResponse<String> resp = HttpSupport.getString(url,
                        java.util.Map.of("Authorization", "Bearer " + key, "Accept", "application/json"));
                int status = resp.statusCode();
                if (status != 200) {
                    if (slim && status == 400) { slim = false; continue; } // retry the same page differently
                    String body = resp.body();
                    if (body.length() > 200) body = body.substring(0, 200);
                    if (status == 401) lastError = "The API key was rejected (401). Check that you copied it correctly.";
                    else if (status == 403) lastError = "Access denied (403). The monthly limit of 1,000 requests may be used up.";
                    else if (status == 429) lastError = "Too many requests (429). Wait a few seconds and try again.";
                    else lastError = "The API returned status " + status + ": " + body;
                    System.out.println(lastError);
                    return new ArrayList<>();
                }
                Map<String, Object> root = (Map<String, Object>) JsonParser.parse(resp.body());
                Map<String, Object> data = (Map<String, Object>) root.get("data");
                List<Object> objects = (List<Object>) data.get("objects");
                for (Object o : objects) {
                    Map<String, Object> m = (Map<String, Object>) o;
                    Country c = new Country();
                    Object n = m.get("names");
                    Object cd = m.get("codes");
                    Object cp = m.get("capitals");
                    if (n instanceof Map) c.name = (String) ((Map<String, Object>) n).get("common");
                    if (cd instanceof Map) c.code = (String) ((Map<String, Object>) cd).get("alpha_2");
                    if (cp instanceof List && !((List<Object>) cp).isEmpty()
                            && ((List<Object>) cp).get(0) instanceof Map)
                        c.capital = (String) ((Map<String, Object>) ((List<Object>) cp).get(0)).get("name");
                    if (c.name != null) list.add(c);
                }
                Object meta = data.get("meta");
                more = meta instanceof Map && Boolean.TRUE.equals(((Map<String, Object>) meta).get("more"));
                offset += 100;
                pages++;
            }
        } catch (java.net.ConnectException | java.net.http.HttpConnectTimeoutException | java.nio.channels.UnresolvedAddressException e) {
            lastError = "Cannot connect to api.restcountries.com. Check your internet connection or firewall.";
        } catch (Exception e) {
            lastError = "Could not read the REST Countries data: " + e;
        }
        if (list.size() < 10 && lastError.isEmpty())
            lastError = "The API returned only " + list.size() + " countries. The demo key only gives sample data - use your own key.";
        if (list.size() >= 10) cache = list; // only cache a successful download
        return list;
    }

    /** Builds a random question for the given mode with the given number of options. */
    @Override
    public Game getRandomGame(String mode, int numOptions) {
        List<Country> all = new ArrayList<>();
        for (Country c : loadCountries()) {
            boolean ok = c.name != null;
            if (mode.equals(MODE_FLAG)) ok = ok && c.code != null;
            else ok = ok && c.capital != null;
            if (ok) all.add(c);
        }
        if (all.size() < numOptions) {
            if (lastError.isEmpty()) lastError = "Not enough country data was returned by the API.";
            return null;
        }

        Country correct = all.get(rnd.nextInt(all.size()));
        boolean answerIsCapital = mode.equals(MODE_CAPITAL);
        String correctText = answerIsCapital ? correct.capital : correct.name;

        Set<String> opts = new LinkedHashSet<>();
        opts.add(correctText);
        while (opts.size() < numOptions) {
            Country c = all.get(rnd.nextInt(all.size()));
            opts.add(answerIsCapital ? c.capital : c.name);
        }
        List<String> options = new ArrayList<>(opts);
        Collections.shuffle(options, rnd);
        int solution = options.indexOf(correctText);

        String question;
        BufferedImage image = null;
        if (mode.equals(MODE_CAPITAL)) {
            question = "What is the capital of " + correct.name + "?";
        } else if (mode.equals(MODE_COUNTRY)) {
            question = "Which country has the capital " + correct.capital + "?";
        } else {
            question = "Which country does this flag belong to?";
            try {
                byte[] png = HttpSupport.getBytes(FLAG_CDN + correct.code.toLowerCase() + ".png");
                if (png == null) throw new java.io.IOException("flag server did not return the image");
                image = ImageIO.read(new ByteArrayInputStream(png));
            } catch (Exception e) {
                lastError = "Could not load the flag image from flags.restcountries.com: " + e;
                System.out.println(lastError);
                return null;
            }
        }
        return new Game(question, image, options, solution);
    }
}
