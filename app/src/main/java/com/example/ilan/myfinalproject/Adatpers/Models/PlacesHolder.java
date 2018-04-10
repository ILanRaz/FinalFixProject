package com.example.ilan.myfinalproject.Adatpers.Models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Others.AppData;
import com.example.ilan.myfinalproject.Requests.Data.ImageDataHolder;
import com.example.ilan.myfinalproject.Requests.Listener.OnLoadListener;
import com.example.ilan.myfinalproject.Others.Place;



// model for holding the list iems
public class PlacesHolder extends RecyclerView.ViewHolder implements OnLoadListener {
    public static final int LAYOUT_RES = R.layout.favourite_place;

    private TextView name, address, distance;
    private ProgressBar loadingIcon;
    private ImageDataHolder iconImageHolder;
    private RatingBar rating;
    private Context context;
    private ImageView icon;


    public PlacesHolder(View itemView) {
        super(itemView);

        context = itemView.getContext();

        name = (TextView) itemView.findViewById(R.id.tv_name);
        address = (TextView) itemView.findViewById(R.id.tv_address);
        distance = (TextView) itemView.findViewById(R.id.tv_distance);

        icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        loadingIcon = (ProgressBar) itemView.findViewById(R.id.pb_loading);

        rating = (RatingBar) itemView.findViewById(R.id.rb_rating);

        iconImageHolder = new ImageDataHolder(icon, this);
    }


    public void bindPlace(Place place) {
        int placeDistance, iconSize;

        name.setText(place.getName());
        address.setText(place.getAddress());

        rating.setRating(place.getRating());

        placeDistance = place.getDistance();

        if (placeDistance != AppData.NULL_DATA) {
            distance.setText(AppData.Preferences.getDistanceText(context, placeDistance));
            distance.setVisibility(View.VISIBLE);
        } else {
            distance.setVisibility(View.GONE);
        }


        iconSize = context.getResources().getDimensionPixelSize(R.dimen.icon_size);

        iconImageHolder.bindImageData(place.getIconImage(), iconSize, iconSize);
    }


    @Override
    public void onPreLoad() {
        loadingIcon.setVisibility(View.VISIBLE);
    }


    @Override
    public void onLoad() {
        loadingIcon.setVisibility(View.GONE);
    }


}
