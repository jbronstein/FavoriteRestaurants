package com.apress.gerber.reminders.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.apress.gerber.reminders.app.Reminder;

/**
 * Created by Adam Gerber on 5/12/2014.
 * University of Chicago
 */
public class RemindersDbAdapter {

    //these are the field names
    public static final String KEY_ID = "_id";
    public static final String KEY_CONTENT = "name";
    public static final String KEY_IMPORTANT = "imp";

    //these are the corresponding indices
    public static final int KEY_ID_INDEX = 0;
    public static final int KEY_CONTENT_INDEX = 1;
    public static final int KEY_IMPORTANT_INDEX = 2;

    //used for logging
    private static final String TAG = "RemindersDbAdapter";


    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "dba_remdr";
    private static final String TABLE_NAME = "tbl_remdr";
    private static final int DATABASE_VERSION = 1;


    private final Context mCtx;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    KEY_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    KEY_CONTENT + " TEXT, " +
                    KEY_IMPORTANT + " INTEGER );";


    public RemindersDbAdapter(Context ctx) {
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
    public void createReminder(String name, boolean important) {

        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, name);
        values.put(KEY_IMPORTANT, important ? 1 : 0);
        mDb.insert(TABLE_NAME, null, values);

    }

    //overloaded to take a reminder
    public long createReminder(Reminder reminder) {

        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, reminder.getContent()); // Contact Name
        values.put(KEY_IMPORTANT, reminder.getImportant()); // Contact Phone Number

        // Inserting Row
        return mDb.insert(TABLE_NAME, null, values);

    }


    //READ
    public Reminder fetchReminderById(int id) {

        Cursor cursor = mDb.query(TABLE_NAME, new String[]{KEY_ID,
                        KEY_CONTENT, KEY_IMPORTANT}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        return new Reminder(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2)

        );


    }

    public Cursor fetchAllReminders() {

        Cursor mCursor = mDb.query(TABLE_NAME, new String[]{KEY_ID,
                        KEY_CONTENT, KEY_IMPORTANT},
                null, null, null, null, null
        );

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }



    //UPDATE
    public void updateReminder(Reminder reminder) {

        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, reminder.getContent());
        values.put(KEY_IMPORTANT, reminder.getImportant());

         mDb.update(TABLE_NAME, values,
                KEY_ID + "=?", new String[]{String.valueOf(reminder.getId())});

    }


    //DELETE
    public void deleteReminderById(int nId) {

        mDb.delete(TABLE_NAME, KEY_ID + "=?", new String[]{String.valueOf(nId)});

    }


    public void deleteAllReminders() {

        mDb.delete(TABLE_NAME, null, null);

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