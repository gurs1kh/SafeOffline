package edu.washington.singhm5.saveoffline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class Url {

    /**
     * An array of user items.
     */
    public static List<UrlInfo> ITEMS = new ArrayList<UrlInfo>();

    /**
     * A map of user items, by email.
     */
    public static Map<String, UrlInfo> ITEM_MAP = new HashMap<String, UrlInfo>();


    private static void addItem(UrlInfo item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.title, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class UrlInfo {
        public String title;
        public String url;

        public UrlInfo(String title, String url) {
            this.title = title;
            this.url = url;
        }

        @Override
        public String toString() {
            return title + ": " + url;
        }
    }
}
