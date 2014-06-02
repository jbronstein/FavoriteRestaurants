package com.jbronstein.profavrestos.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jbronstein.profavrestos.app.Restaurant;

/**
 * Created by Adam Gerber on 5/12/2014.
 * University of Chicago
 */
public class RestaurantDbAdapter {

    //these are the field names
    public static final String KEY_ID = "_id";
    public static final String KEY_CONTENT = "name";
    public static final String KEY_IMPORTANT = "imp";
    public static final String KEY_CITY = "city";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_YELPURL = "yelp_url";
    public static final String KEY_NOTE = "note";

    //these are the corresponding indices
    public static final int KEY_ID_INDEX = 0;
    public static final int KEY_CONTENT_INDEX = 1;
    public static final int KEY_IMPORTANT_INDEX = 2;
    public static final int KEY_CITY_INDEX = 3;
    public static final int KEY_PHONE_INDEX = 4;
    public static final int KEY_ADDRESS_INDEX = 5;
    public static final int KEY_YELPURL_INDEX = 6;
    public static final int KEY_NOTE_INDEX = 7;

    //used for logging
    private static final String TAG = "RestaurantDbAdapter";


    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "dba_resto";
    private static final String TABLE_NAME = "tbl_resto";
    private static final int DATABASE_VERSION = 6;


    private final Context mCtx;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    KEY_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    KEY_CONTENT + " TEXT, " +
                    KEY_IMPORTANT + " INTEGER, " +
                    KEY_CITY + " TEXT, " +
                    KEY_PHONE + " TEXT, " +
                    KEY_ADDRESS + " TEXT, " +
                    KEY_YELPURL + " TEXT, " +
                    KEY_NOTE + " TEXT);";


    public RestaurantDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    //open
    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

    }

    //close
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    //CREATE
    //note that the id will be created for you automatically
    public void createRestaurant(String name, boolean important, String city, String phone, String address, String yelp_url, String notes) {


        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, name);
        values.put(KEY_IMPORTANT, important ? 1 : 0);
        values.put(KEY_CITY, city);
        values.put(KEY_PHONE, phone);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_YELPURL, yelp_url);
        values.put(KEY_NOTE, notes);

        mDb.insert(TABLE_NAME, null, values);
        Log.i("INSERTED: ", values.toString());
    }

    public long createRestaurant(Restaurant restaurant) {

        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, restaurant.getContent());
        values.put(KEY_IMPORTANT, restaurant.getImportant());
        values.put(KEY_CITY, restaurant.getCity());
        values.put(KEY_PHONE, restaurant.getPhone());
        values.put(KEY_ADDRESS, restaurant.getAddress());
        values.put(KEY_YELPURL, restaurant.getUrl());
        values.put(KEY_NOTE, restaurant.getNote());

        // Inserting Row
        return mDb.insert(TABLE_NAME, null, values);

    }


    //READ
    public Restaurant fetchRestaurantById(int id) {

        Cursor cursor = mDb.query(TABLE_NAME, new String[]{KEY_ID,
                        KEY_CONTENT, KEY_IMPORTANT, KEY_CITY, KEY_PHONE, KEY_ADDRESS, KEY_YELPURL, KEY_NOTE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );


        if (cursor != null) {
            cursor.moveToFirst();
        }

        return new Restaurant(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7)

        );


    }


    public Cursor fetchAllRestaurants() {


        Cursor mCursor = mDb.query(TABLE_NAME, new String[]{KEY_ID,
                        KEY_CONTENT, KEY_IMPORTANT, KEY_CITY, KEY_PHONE, KEY_ADDRESS, KEY_YELPURL, KEY_NOTE},
                null, null, null, null, null
        );


        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }



    //UPDATE
    public void updateRestaurant(Restaurant restaurant) {

        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, restaurant.getContent());
        values.put(KEY_IMPORTANT, restaurant.getImportant());
        values.put(KEY_CITY, restaurant.getCity());
        values.put(KEY_PHONE, restaurant.getPhone());
        values.put(KEY_ADDRESS, restaurant.getAddress());
        values.put(KEY_YELPURL, restaurant.getUrl());
        values.put(KEY_NOTE, restaurant.getNote());

         mDb.update(TABLE_NAME, values,
                KEY_ID + "=?", new String[]{String.valueOf(restaurant.getId())});

    }


    //DELETE
    public void deleteRestaurantById(int nId) {

        mDb.delete(TABLE_NAME, KEY_ID + "=?", new String[]{String.valueOf(nId)});

    }


    public void deleteAllRestaurants() {

        mDb.delete(TABLE_NAME, null, null);

    }

    public void insertSomeRestaurants() {

        createRestaurant("Sable", true, "Chicago", "+1-312-755-9704", "505 N State St", "http://www.yelp.com/biz/sable-chicago", "LOVE IT");
        createRestaurant("Yolk", false, "Chicago", "+1-312-787-2277", "747 N Wells", "http://www.yelp.com/biz/yolk-river-north-chicago-2", "MEDIOCRE");
        createRestaurant("Medici On 57th", false, "Chicago", "+1-773-667-7394", "1327 E 57th St", "http://www.yelp.com/biz/medici-on-57th-chicago", "Good Coffee");
    }



    //static inner class
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}