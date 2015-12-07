package edu.washington.singhm5.saveoffline.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTransactionListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
    public static List<UrlInfo> ITEMS = new ArrayList<>();

    /**
     * A map of user items, by email.
     */
    public static Map<String, UrlInfo> ITEM_MAP = new HashMap<>();

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "SavedPages.db";
    private UrlDBHelper mUrlDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Constructor for Url.
     *
     * @param context
     */
    public Url(Context context) {
        mUrlDBHelper = new UrlDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mUrlDBHelper.getWritableDatabase();
    }

    /**
     * Insert a URL to the local database
     *
     * @param title title for the url
     * @param url the url for the page
     * @return the success information
     */
    public boolean insertUrl(String title, String url) {
        ContentValues contentValues = new ContentValues();
        Date date = new Date();
        contentValues.put("title", title);
        contentValues.put("url", url);
        contentValues.put("modified_date", date.getTime());
        contentValues.put("is_deleted", 0);

        long rowId = mSQLiteDatabase.insert("SavedPages", null, contentValues);
        return rowId != -1;
    }

    /**
     * retreive all of the Url that has been stored in the lcoal database.
     *
     * @return List of the url store locally.
     */
    public List<UrlInfo> getAllUrl() {
        ITEMS.clear();
        Cursor c = mSQLiteDatabase.query("SavedPages", null, null, null,
                null, null, null);
        if(c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                //Push URL to array
                UrlInfo temp = new UrlInfo(c.getString(c.getColumnIndex("title")),
                                            c.getString(c.getColumnIndex("url")),
                                            c.getInt(c.getColumnIndex("modified_date")),
                                            c.getInt(c.getColumnIndex("is_deleted")));
                ITEMS.add(temp);
            } while (c.moveToNext());
        }
        return ITEMS;
    }

    /**
     * Change the status of the URL to be deleted before it is removed from the database.
     *
     * @param url the URL to be updated
     * @return the success condition of update
     */
    public boolean softDelete(String url) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_deleted", 1);
        long rowId = mSQLiteDatabase.update("SavedPages", contentValues, "url = ?", new String[] {url});
        return rowId != -1;
    }

    /**
     * completely remove the url from the local database.
     *
     * @param url the url to be deleted
     */
    public void deleteUrl(String url) {
        mSQLiteDatabase.delete("SavedPages", "url = ?", new String[] {url});
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }
    /**
     * A dummy item representing a piece of content.
     */
    public static class UrlInfo {
        private String title;
        private String url;
        private int mod_date;
        private int is_deleted;

        /**
         * Constructor for the UrlInfo
         *
         * @param title the title of the url
         * @param url the url
         * @param mod_date when it is created
         * @param delStat the deletion status of url
         */
        public UrlInfo(String title, String url, int mod_date, int delStat) {
            this.title = title;
            this.url = url;
            this.mod_date = mod_date;
            this.is_deleted = delStat;
        }

        /**
         * retreive the modified date of the url
         * @return the modified date of url
         */
        public int getModDate() {
            return this.mod_date;
        }

        /**
         * retreive the title of the url
         *
         * @return the title of the url
         */
        public String getTitle() {return this.title; }

        /**
         * retreive the url
         *
         * @return the url
         */
        public String getUrl() {return this.url; }

        /**
         * retreive the deletion status of url
         *
         * @return the deletion status
         */
        public int getDeleteStatus() {
            return this.is_deleted;
        }

        @Override
        public String toString() {
            return title + ": " + url + " :" + is_deleted;
        }
    }

    /**
     * Initiates the creation of the database.
     */
    class UrlDBHelper extends SQLiteOpenHelper {
        private static final String CREATE_URL_SQL =
                "CREATE TABLE IF NOT EXISTS SavedPages(" +
                        "id INT AUTO_INCREMENT," +
                        "title TEXT NOT NULL," +
                        "url TEXT NO NULL," +
                        "modified_date INT," + //Since ContentValue cannot do timestamp
                        "is_deleted INT" +
                        ")";

        private static final String DROP_URL_SQL =
                "DROP TABLE IF EXIST SavedPages";

        public UrlDBHelper(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_URL_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {
            sqLiteDatabase.execSQL(DROP_URL_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}
