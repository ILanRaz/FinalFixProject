package com.example.ilan.myfinalproject.DTBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// Database opener and writer
public abstract class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "NearMe.db";
    public static final int DATABASE_VERSION = 1;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Table.Places.TABLE_NAME + "(" + Table.Places.COL_PLACE_ID + " INTEGER PRIMARY KEY, " +
                Table.Places.COL_GOOGLE_ID + " TEXT," +
                Table.Places.COL_NAME + " TEXT," +
                Table.Places.COL_ADDRESS + " TEXT," +
                Table.Places.COL_LAT + " DOUBLE," +
                Table.Places.COL_LNG + " DOUBLE," +
                Table.Places.COL_RATING + " FLOAT," +
                Table.Places.COL_ICON_URL + " TEXT," +
                Table.Places.COL_PHONE + " TEXT," +
                Table.Places.COL_WEBSITE + " TEXT," +
                Table.Places.COL_FAVOURITE + " INTEGER," +
                Table.Places.COL_SEARCH + " INTEGER)" +
                ";");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    protected long insert(String tableName, ContentValues values) throws SQLException {
        long id;
        SQLiteDatabase db = getWritableDatabase();

        try {

            id = db.insertOrThrow(tableName, null, values);

        } finally {
            db.close();
        }

        return id;
    }


    protected boolean updateDatabase(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        boolean flag = true;

        try {

            db.execSQL(sql);

        } catch (Exception e) {
            Log.e("Database", "error in Database-updateDatabase sql=" + sql + " :" + e.getMessage());
            flag = false;
        } finally {
            db.close();
        }

        return flag;
    }


    protected Cursor getCursor(String sql) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, null);

            if (!cursor.moveToFirst()) {
                cursor.close();
                db.close();
                cursor = null;
            }

        } catch (Exception e) {
            Log.e("Database", "error in Database-getCursor sql=" + sql + " :" + e.getMessage());

            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return cursor;
    }


    protected boolean isExist(String sql) {
        boolean isExist = false;
        Cursor cursor = getCursor(sql);

        if (cursor != null) {
            isExist = true;
            cursor.close();
        }

        return isExist;
    }

    // model class
    protected static final class Table {
        public static final int TRUE = 1;
        public static final int FALSE = 0;


        public static class Places {
            public static final String TABLE_NAME = "places";
            public static final String COL_PLACE_ID = "place_id";
            public static final String COL_GOOGLE_ID = "google_id";
            public static final String COL_NAME = "name";
            public static final String COL_ADDRESS = "address";
            public static final String COL_LAT = "lat";
            public static final String COL_LNG = "lng";
            ;
            public static final String COL_RATING = "rating";
            public static final String COL_ICON_URL = "icon_url";
            public static final String COL_PHONE = "phone";
            public static final String COL_WEBSITE = "website";
            public static final String COL_FAVOURITE = "favourite";
            public static final String COL_SEARCH = "search";
        }
    }
}
