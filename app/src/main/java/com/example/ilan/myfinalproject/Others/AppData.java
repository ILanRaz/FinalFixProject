package com.example.ilan.myfinalproject.Others;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.example.ilan.myfinalproject.R;

import java.util.Locale;


// common class wit common and genaric functionalities
public final class AppData {
    public static final String DEFAULT_LANGUAGE = "en_US";
    public static final int NULL_DATA = -1;


    public static void setAppLocale(Context context) {
        String currentLanguage;
        Configuration config;
        Locale defaultLocale;

        currentLanguage = context.getString(R.string.language);

        if (currentLanguage.equals(DEFAULT_LANGUAGE)) {
            defaultLocale = new Locale(DEFAULT_LANGUAGE);

            Locale.setDefault(defaultLocale);

            config = new Configuration();
            config.locale = defaultLocale;

            context.getApplicationContext().getResources().updateConfiguration(config, null);
        }
    }


    public static void sharePlace(Context context, Place place) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, place.getName() + "\n" + place.getAddress());
        shareIntent.setType("text/plain");
        context.startActivity(shareIntent);
    }

//KM/MILES Preferences
    public static final class Preferences {
        public static final String SHARED_PREFERENCES_NAME = "preferences";

        public static final String KEY_DISTANCE_TYPE = "distance_type";
        public static final String KEY_RADIUS = "radius";

        public static final int KM_TYPE = 1;
        public static final int MIL_TYPE = 2;

        public static final int DEFAULT_RADIUS = 500;
        public static final int DEFAULT_DISTANCE_TYPE = KM_TYPE;

        private static final float KILOMETER = 1000;
        private static final float MIL = 1609.344f;


        public static String[] getDistanceTypes(Context context) {
            return new String[]{context.getString(R.string.distance_type_km), context.getString(R.string.distance_type_mil)};
        }


        public static String[] getDistanceTypeValues() {
            return new String[]{String.valueOf(KM_TYPE), String.valueOf(MIL_TYPE)};
        }


        public static String getDistanceTypeText(Context context) {
            int distanceType = getSharedPreferences(context).getInt(KEY_DISTANCE_TYPE, DEFAULT_DISTANCE_TYPE);

            return getDistanceTypeText(context, distanceType);
        }


        public static String getDistanceTypeText(Context context, int distanceType) {
            String distanceTypeText;

            switch (distanceType) {
                case MIL_TYPE:
                    distanceTypeText = context.getString(R.string.distance_type_mil);
                    break;

                case KM_TYPE:
                default:
                    distanceTypeText = context.getString(R.string.distance_type_km);
                    break;

            }

            return distanceTypeText;
        }


        public static int getRadius(Context context) {
            return getSharedPreferences(context).getInt(KEY_RADIUS, DEFAULT_RADIUS);
        }


        public static String getDistanceText(Context context, int distance) {
            SharedPreferences sharedPreferences;
            String distanceText;
            float distanceResult;
            int distanceType;

            sharedPreferences = getSharedPreferences(context);

            distanceType = sharedPreferences.getInt(KEY_DISTANCE_TYPE, DEFAULT_DISTANCE_TYPE);

            switch (distanceType) {
                case MIL_TYPE:
                    distanceText = context.getString(R.string.distance_mil);
                    distanceResult = distance / MIL;
                    break;

                case KM_TYPE:
                default:
                    distanceText = context.getString(R.string.distance_km);
                    distanceResult = distance / KILOMETER;
                    break;

            }

            return String.format(distanceText, distanceResult);
        }


        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
    }

    // using the google places appi to get the place list
    public static final class PlacesAPI {
        private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

        private static final int PHOTO_MAX_SIZE = 1600;

        private static final String SEARCH_REQUEST = "nearbysearch/json?";
        private static final String DETAILS_REQUEST = "details/json?";
        private static final String PHOTO_REQUEST = "photo?";

        private static final String PLACE_ID_FIRST_PARAMETER = "placeid=";
        private static final String LOCATION_FIRST_PARAMETER = "location=";
        private static final String MAX_WIDTH_FIRST_PARAMETER = "maxwidth=";
        private static final String MAX_HEIGHT_FIRST_PARAMETER = "maxheight=";

        private static final String RADIUS_PARAMETER = "&radius=";
        private static final String KEYWORD_PARAMETER = "&keyword=";
        private static final String KEY_PARAMETER = "&key=";
        private static final String PHOTO_REFERENCE_PARAMETER = "&photoreference=";


        private static String getKeywordFormat(String keyword) {
            return keyword.trim().replaceAll("\\s", "%20");
        }

        // return a url to get the list of places whihc relates to the search keyword.
        public static String createSearchUrl(String keyword, double lat, double lng, int radius, String apiKey) {
            String url = BASE_URL;

            url += SEARCH_REQUEST;
            url += LOCATION_FIRST_PARAMETER + lat + "," + lng;
            url += RADIUS_PARAMETER + radius;

            if (keyword != null) {
                url += KEYWORD_PARAMETER + getKeywordFormat(keyword);
            }

            url += KEY_PARAMETER + apiKey;

            //Log.d("AppData-PlacesAPI","url= "+ url);
            return url;
        }

        // return the url to get detail of a specific place
        public static String createDetailsUrl(String googleId, String apiKey) {
            String url = BASE_URL;

            url += DETAILS_REQUEST;
            url += PLACE_ID_FIRST_PARAMETER + googleId;
            url += KEY_PARAMETER + apiKey;

            return url;
        }

        // return the url for the place imsgae
        public static String createPhotoUrl(String photoReference, String apiKey) {
            return createPhotoUrl(photoReference, PHOTO_MAX_SIZE, PHOTO_MAX_SIZE, apiKey);
        }

        // return the url for the place imsgae with specific height width
        public static String createPhotoUrl(String photoReference, int width, int height, String apiKey) {
            String url = BASE_URL;

            url += PHOTO_REQUEST;

            width = (width == NULL_DATA) ? PHOTO_MAX_SIZE : width;
            height = (height == NULL_DATA) ? PHOTO_MAX_SIZE : height;

            url += MAX_WIDTH_FIRST_PARAMETER + width + "&" + MAX_HEIGHT_FIRST_PARAMETER + height;


            url += PHOTO_REFERENCE_PARAMETER + photoReference;
            url += KEY_PARAMETER + apiKey;

            return url;
        }

    }


}
