package com.example.ilan.myfinalproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ilan.myfinalproject.Extra.PlaceMenu;
import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Others.Callback.PlaceDetails;
import com.example.ilan.myfinalproject.Adatpers.FragmentsAdapter;
import com.example.ilan.myfinalproject.Others.AppData;
import com.example.ilan.myfinalproject.DTBase.DatabaseManager;
import com.example.ilan.myfinalproject.Fragments.DetailsFragment;
import com.example.ilan.myfinalproject.Fragments.MapFragment;
import com.example.ilan.myfinalproject.Others.Place;

import java.util.List;


public class DetailsActivity extends ParrentActivity implements PlaceDetails {
    // constant decleared for passing the place id to other activity
    private static final String EXTRA_PLACE_ID = "placeId";

    // adapter for the fragment
    private FragmentsAdapter fragmentsAdapter;
    // getting access to the database
    private DatabaseManager databaseManager;
    // object of the DetailsFragment
    private DetailsFragment detailsFragment;
    // fragment manager oject
    private FragmentManager fragmentManager;
    // google maps  fragment object
    private MapFragment mapFragment;
    private int mapFragmentPosition;
    private ViewPager viewPager;
    private Place place;

    // called to show the deatils of the specific place
    public static void openActivity(Context context, long placeId) {
        // start new activity and pass the place id
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, placeId);
        context.startActivity(intent);
    }


    // called when activity is created
    @Override
    protected void onCreate() {
        long placeId;

        // initilize objects with null refference
        detailsFragment = null;
        mapFragment = null;

        // getting instance of the database
        databaseManager = DatabaseManager.getInstance(this);

        // getting the placeid recieved
        placeId = getIntent().getLongExtra(EXTRA_PLACE_ID, AppData.NULL_DATA);

        // getting the place object from database from placeid
        place = databaseManager.getPlace(placeId);
    }


    // called whenfragment is created/loaded
    @Override
    protected void onCreateFragments() {
        List<Fragment> fragments;

        fragmentManager = getSupportFragmentManager();

        fragments = fragmentManager.getFragments();

        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof DetailsFragment) {
                    detailsFragment = (DetailsFragment) fragment;
                    continue;
                }

                if (fragment instanceof MapFragment) {
                    mapFragment = (MapFragment) fragment;
                    continue;
                }
            }

        }

        if (detailsFragment == null) {
            detailsFragment = DetailsFragment.newInstance(place.getId());
        }

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(place.getId());
        }

        fragmentsAdapter = new FragmentsAdapter(this, fragmentManager, detailsFragment, mapFragment);

        mapFragmentPosition = fragmentsAdapter.getItemPosition(mapFragment);
    }

    // called when view created
    @Override
    protected void onCreateView() {
        ActionBar actionBar;

        setContentView(R.layout.place);

        // setting toolbar as a android action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        // set title of the toolbar
        actionBar.setTitle(place.getName());
        // enable back button
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set the viewpager to display fragments
        viewPager = (ViewPager) findViewById(R.id.ViewPager);
        viewPager.setAdapter(fragmentsAdapter);

        // setup tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_tabs);
        tabLayout.setupWithViewPager(viewPager);

        ViewCompat.setLayoutDirection(tabLayout, ViewCompat.LAYOUT_DIRECTION_LTR);
    }


    // used for loading options to the toolbar / Actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        PlaceMenu.createPlaceMenu(menu, getMenuInflater(), place.isFavourite());
        return true;
    }

    // when a specific item / menu is seleted from the actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isFavouritePlace;

        switch (item.getItemId()) {

            case R.id.m_favourite_toggle:
                isFavouritePlace = !(place.isFavourite());

                databaseManager.updatePlace(place.getId(), isFavouritePlace);
                place.setFavourite(isFavouritePlace);

                if (isFavouritePlace) {
                    item.setIcon(R.drawable.ic_menu_star);
                } else {
                    item.setIcon(R.drawable.ic_menu_star_border);
                }
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.m_share:
                AppData.sharePlace(this, place);
                break;

        }

        return true;
    }

    // when the location changes interface is called
    @Override
    public void onLocationChanged(Location location) {
        // move to the new location
        detailsFragment.onLocationChanged(location);
        mapFragment.onLocationChanged(location);
    }

    // returns the curent location
    @Override
    public Location getCurrentLocation() {
        return getLocation();
    }


    @Override
    public void onClickDetail() {
        // when a place is clicked
        viewPager.setCurrentItem(mapFragmentPosition, true);
        mapFragment.animatePlaceMarker();
    }
}
