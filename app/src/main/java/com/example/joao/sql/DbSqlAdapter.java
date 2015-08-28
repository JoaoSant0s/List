package com.example.joao.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by Joao on 12/08/2015.
 */
public final class DbSqlAdapter {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyDb.db";

    public static final int COL_ROWID = 0;
    public static final int COL_NAME = 1;

    private static final String TEXT_TYPE = " text not null";
    private static final String SQL_CREATE_DATABASE = "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
            FeedEntry.KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + ");";

    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public static final String[] ALL_KEYS = new String[] {FeedEntry.KEY_ROWID, FeedEntry.COLUMN_NAME_TITLE};


    public DbSqlAdapter(Context ctx){
        this.context = ctx;
        myDBHelper = new DatabaseHelper(this.context);
    }

    // Open the database connection.
    public DbSqlAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(String name) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(FeedEntry.COLUMN_NAME_TITLE, name);

        // Insert it into the database.
        return db.insert(FeedEntry.TABLE_NAME, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = FeedEntry.KEY_ROWID + "=" + rowId;
        return db.delete(FeedEntry.TABLE_NAME, where, null) != 0;
    }

    // Return all data in the database.
    public ArrayList<String> getAllRows() {
        ArrayList<String> h = new ArrayList<>();
        String where = null;
        Cursor c = db.query(true, FeedEntry.TABLE_NAME, ALL_KEYS, where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {
                    h.add(c.getString(COL_NAME));
                } while(c.moveToNext());
            }
        }
        return h;
    }


    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = FeedEntry.KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, FeedEntry.TABLE_NAME, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(long rowId, String name) {
        String where = FeedEntry.KEY_ROWID + "=" + rowId;

        ContentValues newValues = new ContentValues();
        newValues.put(FeedEntry.COLUMN_NAME_TITLE, name);

        // Insert it into the database.
        return db.update(FeedEntry.TABLE_NAME, newValues, where, null) != 0;
    }

    public void clearRowset(){
        db.delete(FeedEntry.TABLE_NAME,null, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            System.out.println(SQL_CREATE_DATABASE);
            _db.execSQL(SQL_CREATE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME);
            // Recreate new database:
            onCreate(_db);
        }
    }

    public static abstract class FeedEntry implements BaseColumns{
        public static final String TABLE_NAME = "LISTA";
        public static final String KEY_ROWID = "_id";
        public static final String COLUMN_NAME_TITLE = "TITLE";
    }
}