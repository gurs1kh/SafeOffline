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

    public Url(Context context) {
        mUrlDBHelper = new UrlDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mUrlDBHelper.getWritableDatabase();
    }

    private static void addItem(UrlInfo item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.title, item);
    }

    public boolean inserUrl(String title, String url) {
        ContentValues contentValues = new ContentValues();
        Date date = new Date();
        contentValues.put("title", title);
        contentValues.put("url", url);
        contentValues.put("modified_date", date.getTime());

        long rowId = mSQLiteDatabase.insert("SavedPages", null, contentValues);
        return rowId != -1;
    }

    public List<UrlInfo> getAllUrl() {
        Cursor c = mSQLiteDatabase.query("SavedPages", null, null, null,
                                        null, null, null);
        if(c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                //Push URL to array
                UrlInfo temp = new UrlInfo(c.getString(c.getColumnIndex("title")),
                                            c.getString(c.getColumnIndex("url")));
                ITEMS.add(temp);
            } while (c.moveToNext());
        }
        return ITEMS;
    }

    public boolean deleteUrl() {

        return true;
    }

    public void closeDB() {
        mSQLiteDatabase.close();
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

    class UrlDBHelper extends SQLiteOpenHelper {
        private static final String CREATE_URL_SQL =
                "CREATE TABLE IF NOT EXIST SavedPages(" +
                        "id INT(6)  UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                        "title VARCHAR(30) NOT NULL," +
                        "url TEXT NO NULL," +
                        "modified_date INT" + //Since ContentValue cannot do timestamp
                        "delete BOOLEAN" +
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
