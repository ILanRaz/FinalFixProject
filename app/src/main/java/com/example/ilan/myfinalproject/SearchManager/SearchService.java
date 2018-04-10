package com.example.ilan.myfinalproject.SearchManager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Others.AppData;
import com.example.ilan.myfinalproject.DTBase.DatabaseManager;
import com.example.ilan.myfinalproject.Extra.Json;

import org.json.JSONArray;
import org.json.JSONObject;

// service class get the result of the search
public class SearchService extends IntentService
{
public static final String ACTION_SEARCH="com.example.ilan.myfinalproject.search";

private static final String EXTRA_KEYWORD="keyword";
private static final String EXTRA_LAT="lat";
private static final String EXTRA_LNG="lng";

public static final String EXTRA_STATUS="status";

public static final int STATUS_OK=0;
public static final int STATUS_ZERO_RESULTS=1;
public static final int STATUS_OVER_QUERY_LIMIT=2;
public static final int STATUS_ERROR=3;



    public static void startActionSearch(Context context, String keyword, double lat, double lng)
    {
    Intent intent=new Intent(context, SearchService.class);
    intent.setAction(ACTION_SEARCH);
    intent.putExtra(EXTRA_KEYWORD, keyword);
    intent.putExtra(EXTRA_LAT, lat);
    intent.putExtra(EXTRA_LNG, lng);
    context.startService(intent);
    }


    public SearchService()
    {
    super("SearchService");
    }



    @Override
    protected void onHandleIntent(Intent intent)
    {
    String action;

    if(intent!=null)
        {
        action=intent.getAction();

        if(action.equals(ACTION_SEARCH))
            {
            handleActionSearch(intent.getStringExtra(EXTRA_KEYWORD),intent.getDoubleExtra(EXTRA_LAT, 0),intent.getDoubleExtra(EXTRA_LNG, 0));
            }
        }
    }


    private void handleActionSearch(String keyword, double lat, double lng)
    {
    JSONObject json,place,location,placeDetails,jsonPlaceDetails;
    String apiKey,url,jsonStatus,placeId,phone,website,iconUrl;
    DatabaseManager databaseManager;
    JSONArray results;
    Intent intent;
    float rating;
    int status;


    databaseManager=DatabaseManager.getInstance(this);

    status=STATUS_ERROR;

    apiKey=getString(R.string.places_api_key);

    url=AppData.PlacesAPI.createSearchUrl(keyword, lat , lng, AppData.Preferences.getRadius(this), apiKey);

    try {
        json=new JSONObject(Json.getJsonResult(url));
        results=json.getJSONArray("results");

        if(results.length() > 0)
            {
            databaseManager.deleteLastSearch();
            }

        for(int i=0 ; i < results.length() ; i++)
                {
                place=results.getJSONObject(i);

                placeId=place.getString("place_id");

                jsonPlaceDetails=new JSONObject( Json.getJsonResult( AppData.PlacesAPI.createDetailsUrl( placeId, apiKey ) ) );

                placeDetails=null;

                try {
                    placeDetails=jsonPlaceDetails.getJSONObject("result");
                    }catch(Exception e)
                        {
                        jsonStatus= json.getString("status");

                        if(jsonStatus.equals("OVER_QUERY_LIMIT"))
                            {
                            status=STATUS_OVER_QUERY_LIMIT;
                            }

                        throw e;
                        }

                phone=null;
                website=null;
                rating=0;

                if(placeDetails.has("international_phone_number"))
                    {
                    phone=placeDetails.getString("international_phone_number");
                    }

                if(placeDetails.has("website"))
                    {
                    website=placeDetails.getString("website");
                    }

                if(placeDetails.has("rating"))
                    {
                    rating=(float) place.getDouble("rating");
                    }

                location=placeDetails.getJSONObject("geometry").getJSONObject("location");

                databaseManager.insertSearchPlace(placeId, placeDetails.getString("name"), placeDetails.getString("vicinity"), location.getDouble("lat"), location.getDouble("lng"), rating, placeDetails.getString("icon"), phone, website);
                }

        jsonStatus= json.getString("status");

        if(jsonStatus.equals("OK"))
            {
            status=STATUS_OK;
            }else if(jsonStatus.equals("ZERO_RESULTS"))
                {
                status=STATUS_ZERO_RESULTS;
                }else if(jsonStatus.equals("OVER_QUERY_LIMIT"))
                    {
                    status=STATUS_OVER_QUERY_LIMIT;
                    }

        }catch(Exception e)
            {
            e.printStackTrace();
            }

    intent=new Intent(ACTION_SEARCH);
    intent.putExtra(EXTRA_STATUS , status);

    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
