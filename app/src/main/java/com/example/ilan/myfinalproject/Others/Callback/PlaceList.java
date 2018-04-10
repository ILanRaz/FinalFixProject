package com.example.ilan.myfinalproject.Others.Callback;

import android.support.v4.app.Fragment;

import com.example.ilan.myfinalproject.Others.Place;


// used when the list is retrived using the search
public interface PlaceList extends Location {
    // called when a placeis clicked
    void onClickPlace(Place place);

    // placed removed from the favourates.
    void onRemoveFavouritePlace(Fragment fragment, long placeId);

    // place added to favourates.
    void onAddFavouritePlace(Place place);
}
