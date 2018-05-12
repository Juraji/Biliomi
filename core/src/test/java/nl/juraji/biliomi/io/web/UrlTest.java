package nl.juraji.biliomi.io.web;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Juraji on 24-4-2017.
 * Biliomi v3
 */
public class UrlTest {

    @Test
    public void unpackQueryString() {
        String url = "https://api.twitch.tv/kraken/channels/test_id/subscriptions?offset=500&limit=100&direction=desc";
        Map<String, String> queryMap = Url.unpackQueryString(url);

        assertTrue("The unpacked querymap should contain the key offset", queryMap.containsKey("offset"));
        assertTrue("The unpacked querymap should contain the key limit", queryMap.containsKey("limit"));
        assertTrue("The unpacked querymap should contain the key direction", queryMap.containsKey("direction"));

        assertEquals("The value for offset should equal 500", "500", queryMap.get("offset"));
        assertEquals("The value for limit should equal 100", "100", queryMap.get("limit"));
        assertEquals("The value for direction should equal desc", "desc", queryMap.get("direction"));
    }

    @Test
    public void createQueryString() {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("offset", "500");
        queryMap.put("limit", "100");
        queryMap.put("direction", "desc");
        queryMap.put("weird", ";This; :is: [a] /test/");

        String queryString = Url.createQueryString(queryMap);
        assertEquals("The querystring should be correctly compiled", "weird=%3BThis%3B+%3Ais%3A+%5Ba%5D+%2Ftest%2F&offset=500&limit=100&direction=desc", queryString);
    }

    @Test
    public void appendQueryString() {
        String baseUri = "https://api.twitch.tv/kraken/channels/test_id/subscriptions";
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("offset", "500");
        queryMap.put("limit", "100");
        queryMap.put("direction", "desc");
        queryMap.put("weird", ";This; :is: [a] /test/");

        String uri = Url.appendQueryString(baseUri, queryMap);
        assertEquals("The querystring should be correctly compiled and appended to the baseUri", "https://api.twitch.tv/kraken/channels/test_id/subscriptions?weird=%3BThis%3B+%3Ais%3A+%5Ba%5D+%2Ftest%2F&offset=500&limit=100&direction=desc", uri);
    }

    @Test
    public void urlEncodeUTF8() {
        String nonAsci = ";This; :is: [a] /test/";

        String encoded = Url.urlEncodeUTF8(nonAsci);
        assertEquals("The given string should be correctly escaped for usage in uris", "%3BThis%3B+%3Ais%3A+%5Ba%5D+%2Ftest%2F", encoded);
    }
}