package com.example.ilan.myfinalproject.Adatpers.Models;

import android.view.View;
import android.widget.ImageView;

import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Others.Place;



// model for holding the list items
public class SearchHolder extends PlacesHolder {
    public static final int LAYOUT_RES = R.layout.search_place_item;

    private ImageView favouriteIcon;


    public SearchHolder(View itemView) {
        super(itemView);

        favouriteIcon = (ImageView) itemView.findViewById(R.id.iv_favourite_icon);
    }


    @Override
    public void bindPlace(Place place) {
        super.bindPlace(place);

        int visible = View.GONE;

        if (place.isFavourite()) {
            visible = View.VISIBLE;
        }

        favouriteIcon.setVisibility(visible);
    }
}
