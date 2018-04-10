package com.example.ilan.myfinalproject.DTBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.example.ilan.myfinalproject.Others.Place;

import java.util.ArrayList;


// Get access to database using this class
public class DatabaseManager extends DatabaseSQL {
    private static DatabaseManager instance;


    public synchronized static DatabaseManager getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseManager(context);
        }

        return instance;
    }


    private DatabaseManager(Context context) {
        super(context);
    }


    public void insertSearchPlace(String googleId, String name, String address, double lat, double lng, float rating, String iconUrl, String phone, String website) {
        ContentValues values;
        boolean isExist;

        isExist = isExist(Database.Table.Places.TABLE_NAME, addColStatement(Database.Table.Places.COL_GOOGLE_ID, googleId, true));

        if (!isExist) {
            try {
                values = new ContentValues();
                values.put(Database.Table.Places.COL_GOOGLE_ID, googleId);
                values.put(Database.Table.Places.COL_NAME, name);
                values.put(Database.Table.Places.COL_ADDRESS, address);
                values.put(Database.Table.Places.COL_LAT, lat);
                values.put(Database.Table.Places.COL_LNG, lng);
                values.put(Database.Table.Places.COL_RATING, rating);
                values.put(Database.Table.Places.COL_ICON_URL, iconUrl);
                values.put(Database.Table.Places.COL_PHONE, phone);
                values.put(Database.Table.Places.COL_WEBSITE, website);
                values.put(Database.Table.Places.COL_FAVOURITE, Database.Table.FALSE);
                values.put(Database.Table.Places.COL_SEARCH, Database.Table.TRUE);

                insert(Database.Table.Places.TABLE_NAME, values);
            } catch (SQLException e) {
                Log.e("DatabaseManager", "Error: Failed to insert place into places table");
                e.printStackTrace();
            }
        } else {
            updateFromTable(Database.Table.Places.TABLE_NAME, addColStatement(Database.Table.Places.COL_SEARCH, String.valueOf(Database.Table.TRUE), true),
                    addColStatement(Database.Table.Places.COL_GOOGLE_ID, googleId, true));

        }
    }


    public void updatePlace(long placeId, boolean isFavourite) {
        String updateCols, whereCols;

        updateCols = addColStatement(Database.Table.Places.COL_FAVOURITE, String.valueOf(isFavourite ? Database.Table.TRUE : Database.Table.FALSE), false);
        whereCols = addColStatement(Database.Table.Places.COL_PLACE_ID, String.valueOf(placeId), false);

        updateFromTable(Database.Table.Places.TABLE_NAME, updateCols, whereCols);
    }


    public void updatePlaceWithDelete(long placeId, boolean isFavourite) {
        updatePlace(placeId, isFavourite);

        if (!isFavourite) {
            deleteFromTable(Database.Table.Places.TABLE_NAME, addColStatement(Database.Table.Places.COL_PLACE_ID, String.valueOf(placeId), false) + addColStatement(Database.Table.Places.COL_SEARCH, String.valueOf(Database.Table.FALSE), false, OPERATOR_AND));
        }
    }


    /**
     * where col name = TRUE
     *
     * @param whereColName COL_FAVOURITE or COL_SEARCH
     */
    private ArrayList<Place> getPlaces(String whereColName) {
        int placeIdIndex, googleIdIndex, nameIndex, addressIndex, latIndex, lngIndex, ratingIndex, iconUrlIndex, phoneIndex, websiteIndex, isFavouriteIndex;
        ArrayList<Place> places = null;
        Cursor cursor;

        cursor = selectFromTable(Database.Table.Places.TABLE_NAME, null, addColStatement(whereColName, String.valueOf(Database.Table.TRUE), false));

        if (cursor != null) {
            places = new ArrayList<>();

            placeIdIndex = cursor.getColumnIndex(Database.Table.Places.COL_PLACE_ID);
            googleIdIndex = cursor.getColumnIndex(Database.Table.Places.COL_GOOGLE_ID);
            nameIndex = cursor.getColumnIndex(Database.Table.Places.COL_NAME);
            addressIndex = cursor.getColumnIndex(Database.Table.Places.COL_ADDRESS);
            latIndex = cursor.getColumnIndex(Database.Table.Places.COL_LAT);
            lngIndex = cursor.getColumnIndex(Database.Table.Places.COL_LNG);
            ratingIndex = cursor.getColumnIndex(Database.Table.Places.COL_RATING);
            iconUrlIndex = cursor.getColumnIndex(Database.Table.Places.COL_ICON_URL);
            phoneIndex = cursor.getColumnIndex(Database.Table.Places.COL_PHONE);
            websiteIndex = cursor.getColumnIndex(Database.Table.Places.COL_WEBSITE);
            isFavouriteIndex = cursor.getColumnIndex(Database.Table.Places.COL_FAVOURITE);

            do {

                places.add(new Place(cursor.getLong(placeIdIndex),
                        cursor.getString(googleIdIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(addressIndex),
                        cursor.getDouble(latIndex),
                        cursor.getDouble(lngIndex),
                        cursor.getFloat(ratingIndex),
                        cursor.getString(iconUrlIndex),
                        cursor.getString(phoneIndex),
                        cursor.getString(websiteIndex),
                        cursor.getInt(isFavouriteIndex) == Database.Table.TRUE
                ));

            } while (cursor.moveToNext());

            cursor.close();
        }

        return places;
    }


    public ArrayList<Place> getFavouritePlaces() {
        return getPlaces(Database.Table.Places.COL_FAVOURITE);
    }


    public ArrayList<Place> getLastSearch() {
        return getPlaces(Database.Table.Places.COL_SEARCH);
    }


    public void deleteLastSearch() {
        updateFromTable(Database.Table.Places.TABLE_NAME, addColStatement(Database.Table.Places.COL_SEARCH, String.valueOf(Database.Table.FALSE), false), null);
        deleteFromTable(Database.Table.Places.TABLE_NAME, addColStatement(Database.Table.Places.COL_FAVOURITE, String.valueOf(Database.Table.FALSE), false));
    }


    public void deleteAllFavourites() {
        updateFromTable(Database.Table.Places.TABLE_NAME, addColStatement(Database.Table.Places.COL_FAVOURITE, String.valueOf(Database.Table.FALSE), false), null);
        deleteFromTable(Database.Table.Places.TABLE_NAME, addColStatement(Database.Table.Places.COL_SEARCH, String.valueOf(Database.Table.FALSE), false));
    }


    public Place getPlace(long placeId) {
        Cursor cursor;
        Place place = null;

        cursor = selectFromTable(Database.Table.Places.TABLE_NAME, null, addColStatement(Database.Table.Places.COL_PLACE_ID, String.valueOf(placeId), false));

        if (cursor != null) {
            place = new Place(cursor.getLong(cursor.getColumnIndex(Database.Table.Places.COL_PLACE_ID)),
                    cursor.getString(cursor.getColumnIndex(Database.Table.Places.COL_GOOGLE_ID)),
                    cursor.getString(cursor.getColumnIndex(Database.Table.Places.COL_NAME)),
                    cursor.getString(cursor.getColumnIndex(Database.Table.Places.COL_ADDRESS)),
                    cursor.getDouble(cursor.getColumnIndex(Database.Table.Places.COL_LAT)),
                    cursor.getDouble(cursor.getColumnIndex(Database.Table.Places.COL_LNG)),
                    cursor.getFloat(cursor.getColumnIndex(Database.Table.Places.COL_RATING)),
                    cursor.getString(cursor.getColumnIndex(Database.Table.Places.COL_ICON_URL)),
                    cursor.getString(cursor.getColumnIndex(Database.Table.Places.COL_PHONE)),
                    cursor.getString(cursor.getColumnIndex(Database.Table.Places.COL_WEBSITE)),
                    cursor.getInt(cursor.getColumnIndex(Database.Table.Places.COL_FAVOURITE)) == Database.Table.TRUE
            );
        }


        return place;
    }


    public boolean isFavouritePlace(long placeId) {
        return isExist(Database.Table.Places.TABLE_NAME, addColStatement(Database.Table.Places.COL_PLACE_ID, String.valueOf(placeId), false) +
                addColStatement(Database.Table.Places.COL_FAVOURITE, String.valueOf(Database.Table.TRUE), false, OPERATOR_AND));
    }

}