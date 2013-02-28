package me.shakiba.cambridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class CambridgeAPI1 {

    protected static final Gson deserializer = new Gson();
    protected static final String BASE_URL = "https://dictionary.cambridge.org/api/v1/";
    protected static final String CONTENT_TYPE_JSON = "application/json";
    protected static final String CONTENT_TYPE_XML = "application/xml";

    private final String apiKey;

    public CambridgeAPI1(String apiKey) {
        this.apiKey = apiKey;
    }

    protected String execute(String url, Set<Map.Entry<String, Object>> params,
            String accessKey) throws IOException {
        URL jurl = new URL(append(url, params));
        URLConnection conn = jurl.openConnection();
        conn.setRequestProperty("accessKey", accessKey);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);
        conn.connect();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "UTF-8"));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            content.append("\n" + line);
        }
        return content.substring(Math.min(1, content.length()));
    }

    protected String append(String url, Set<Map.Entry<String, Object>> params)
            throws UnsupportedEncodingException {
        String joiner = "?";
        for (Map.Entry<String, Object> entry : params) {
            url += joiner + entry.getKey() + "=";
            url += URLEncoder.encode(entry.getValue() == null ? "" : entry
                    .getValue().toString(), "UTF-8");
            joiner = "&";
        }
        return url;
    }

    protected <E> E deserialize(String content, Class<E> clazz)
            throws IOException, CambridgeAPI1Exception {
        try {
            Error error = deserializer.fromJson(content, Error.class);
            if (error.errorCode != null || error.errorMessag != null) {
                throw new CambridgeAPI1Exception(error.errorCode,
                        error.errorMessag);
            }
        } catch (JsonSyntaxException e) {
        }
        try {
            return deserializer.fromJson(content, clazz);
        } catch (JsonSyntaxException e) {
            throw new IOException("Invalid response: " + content);
        }
    }

    private Req req(String url) {
        return new Req(url);
    }

    private class Req {
        private Set<Map.Entry<String, Object>> params = new HashSet<Map.Entry<String, Object>>();
        private final String url;

        public Req(String url) {
            this.url = url;
        }

        public Req query(String key, Object value) {
            params.add(new AbstractMap.SimpleEntry<String, Object>(key, value));
            return this;
        }

        public <E> E execute(Class<E> clazz) throws IOException,
                CambridgeAPI1Exception {
            String content = CambridgeAPI1.this.execute(BASE_URL + url, params,
                    apiKey);
            return CambridgeAPI1.this.deserialize(content, clazz);
        }
    }

    public static class CambridgeAPI1Exception extends Exception {

        private static final long serialVersionUID = 1L;

        private final String errorCode;
        private final String errorMessage;

        public CambridgeAPI1Exception(String errorCode, String errorMessage) {
            super(errorMessage);
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public Dictionary[] dictionaries() throws IOException,
            CambridgeAPI1Exception {
        return req("dictionaries").execute(Dictionary[].class);
    }

    public Dictionary dictionary(String dictCode) throws IOException,
            CambridgeAPI1Exception {
        return req("dictionaries/" + dictCode).execute(Dictionary.class);
    }

    public SearchEntries searchEntries(String dictCode, String q, int pagesize,
            int pageindex) throws IOException, CambridgeAPI1Exception {
        return req("dictionaries/" + dictCode + "/search").query("q", q)
                .query("pagesize", pagesize).query("pageindex", pageindex)
                .execute(SearchEntries.class);
    }

    public Suggestions didYouMean(String dictCode, String q, int entrynumber)
            throws IOException, CambridgeAPI1Exception {
        return req("dictionaries/" + dictCode + "/search/didyoumean")
                .query("q", q).query("entrynumber", entrynumber)
                .execute(Suggestions.class);
    }

    public Entry searchFirst(String dictCode, String q, Format format)
            throws IOException, CambridgeAPI1Exception {
        return req("dictionaries/" + dictCode + "/search/first").query("q", q)
                .query("format", format).execute(Entry.class);
    }

    public Entry entry(String dictCode, String entryId, Format format)
            throws IOException, CambridgeAPI1Exception {
        return req("dictionaries/" + dictCode + "/entries/" + entryId).query(
                "format", format).execute(Entry.class);
    }

    public NearbyEntries nearbyEntries(String dictCode, String entryId,
            int entrynumber) throws IOException, CambridgeAPI1Exception {
        return req(
                "dictionaries/" + dictCode + "/entries/" + entryId
                        + "/nearbyentries").query("entrynumber", entrynumber)
                .execute(NearbyEntries.class);
    }

    public Pronunciation[] pronunciations(String dictCode, String entryId)
            throws IOException, CambridgeAPI1Exception {
        return pronunciations(dictCode, entryId, null);
    }

    public Pronunciation[] pronunciations(String dictCode, String entryId,
            Lang lang) throws IOException, CambridgeAPI1Exception {
        return req(
                "dictionaries/" + dictCode + "/entries/" + entryId
                        + "/pronunciations").query("lang", lang).execute(
                Pronunciation[].class);
    }

    public RelatedEntries relatedEntries(String dictCode, String entryId)
            throws IOException, CambridgeAPI1Exception {
        return req(
                "dictionaries/" + dictCode + "/entries/" + entryId
                        + "/relatedentries").execute(RelatedEntries.class);
    }

    public Thesaurus[] topics(String dictCode) throws IOException,
            CambridgeAPI1Exception {
        return req("dictionaries/" + dictCode + "/topics").execute(
                Thesaurus[].class);
    }

    public TopicFull topic(String dictCode, String thesaurusName, String topicId)
            throws IOException, CambridgeAPI1Exception {
        return req(
                "dictionaries/" + dictCode + "/topics/" + thesaurusName + "/"
                        + topicId).execute(TopicFull.class);
    }

    public Entry wordOfTheDay(String dictCode, String day, Format format)
            throws IOException, CambridgeAPI1Exception {
        return req("dictionaries/" + dictCode + "/wordoftheday")
                .query("day", day).query("format", format).execute(Entry.class);
    }

    public Entry wordOfTheDay(String day, Format format) throws IOException,
            CambridgeAPI1Exception {
        return req("wordoftheday").query("day", day).query("format", format)
                .execute(Entry.class);
    }

    public EntryPreview wordOfTheDayPreview(String dictCode, String day)
            throws IOException, CambridgeAPI1Exception {
        return req("dictionaries/" + dictCode + "/wordoftheday/preview").query(
                "day", day).execute(EntryPreview.class);
    }

    public EntryPreview wordOfTheDayPreview(String day) throws IOException,
            CambridgeAPI1Exception {
        return req("wordoftheday/preview").query("day", day).execute(
                EntryPreview.class);
    }

    public static class Dictionary {
        public String dictionaryName;
        public String dictionaryCode;
        public String dictionaryUrl;
    }

    public static class Entry extends Result {
        public String dictionaryCode;
        public String format;
        public String entryContent;
        public Topic[] topics = {};
    }

    public static class EntryPreview extends Result {

        public String dictionaryCode;
        public String format;

        public String htmlEntryPreview;
        public String textEntryPreview;
    }

    public static class NearbyEntries {
        public String dictionaryCode;
        public String entryId;
        public Result[] nearbyPrecedingEntries = {};
        public Result[] nearbyFollowingEntries = {};
    }

    public static class Pronunciation {
        public String dictionaryCode;
        public String entryId;
        public String lang;
        public String pronunciationUrl;
    }

    public static class Related extends Result {
        public String dictionaryCode;
        public String dictionaryName;
    }

    public static class RelatedEntries {
        public String dictionaryCode;
        public String entryId;
        public Related[] relatedEntries = {};
    }

    public static class Result {
        public String entryLabel;
        public String entryUrl;
        public String entryId;
    }

    public static class SearchEntries {
        public String dictionaryCode;
        public Result[] results = {};
        public int resultNumber;
        public int currentPageIndex;
        public int pageNumber;
    }

    public static class Suggestions {
        public String dictionaryCode;
        public String searchTerm;
        public String[] suggestions = {};
    }

    public static class TopicFull extends Topic {
        public String dictionaryCode;
        public Result[] entries = {};
        public Subtopic[] subTopics = {};
    }

    public static class Subtopic {
        public String subTopicId;
        public String subTopicLabel;
        public String subTopicUrl;
    }

    public static class Topic {
        public String topicId;
        public String topicThesaurusName;
        public String topicLabel;
        public String topicUrl;
        public String topicParentId;
    }

    public static class Thesaurus {
        public String thesaurus;
    }

    public static class Error {
        public String errorCode;
        public String errorMessag;
    }

    public enum Format {
        HTML, XML;
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum Lang {
        US, UK;
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static final String DICT_BRITISH = "british";
    public static final String DICT_TURKISH = "turkish";
    public static final String DICT_AMERICAN_ENGLISH = "american-english";
    public static final String DICT_BUSINESS_ENGLISH = "business-english";
    public static final String DICT_LEARNER_ENGLISH = "learner-english";

}
