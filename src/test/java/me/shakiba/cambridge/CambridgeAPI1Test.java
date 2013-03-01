package me.shakiba.cambridge;

import java.util.prefs.Preferences;

import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CambridgeAPI1Test {

    private CambridgeAPI1 api;

    public CambridgeAPI1Test() throws Exception {
        String apiKey = Preferences.userRoot().node("/org/cambridge/api")
                .get("apiKey", null);
        if (apiKey == null) {
            throw new Exception("API Key is not set.");
        }
        api = new CambridgeAPI1(apiKey);
    }

    public void dictionaries() throws Exception {
        view(api.dictionaries());
    }

    public void dictionary() throws Exception {
        view(api.dictionary(CambridgeAPI1.DICT_BRITISH));
    }

    public void searchEntries() throws Exception {
        view(api.searchEntries(CambridgeAPI1.DICT_BRITISH, "apple", 10, 1));
    }

    public void didYouMean() throws Exception {
        view(api.didYouMean(CambridgeAPI1.DICT_BRITISH, "googel", 5));
    }

    public void searchFirst() throws Exception {
        view(api.searchFirst(CambridgeAPI1.DICT_BRITISH, "apple",
                CambridgeAPI1.Format.HTML));
    }

    public void entry() throws Exception {
        view(api.entry(CambridgeAPI1.DICT_BRITISH, "apple",
                CambridgeAPI1.Format.HTML));
    }

    public void nearbyEntries() throws Exception {
        view(api.nearbyEntries(CambridgeAPI1.DICT_BRITISH, "apple", 2));
    }

    public void pronunciations() throws Exception {
        view(api.pronunciations(CambridgeAPI1.DICT_BRITISH, "apple"));
    }

    public void relatedEntries() throws Exception {
        view(api.relatedEntries(CambridgeAPI1.DICT_BRITISH, "apple"));
    }

    public void topics() throws Exception {
        view(api.topics(CambridgeAPI1.DICT_BRITISH));
    }

    @Test
    public void topic() throws Exception {
        view(api.topic(CambridgeAPI1.DICT_BRITISH, "topics", "business"));
        view(api.topic(CambridgeAPI1.DICT_BRITISH, "topics", "cars"));
    }

    public void wordOfTheDay() throws Exception {
    }

    public void wordOfTheDayPreview() throws Exception {
    }

    public void view(Object obj) {
        System.out.println(gson.toJson(obj));
    }

    private static Gson gson = new GsonBuilder().setPrettyPrinting()
            .serializeNulls().create();

}