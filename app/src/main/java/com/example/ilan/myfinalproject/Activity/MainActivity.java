package com.example.ilan.myfinalproject.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ilan.myfinalproject.Adatpers.FragmentsAdapter;
import com.example.ilan.myfinalproject.DTBase.DatabaseManager;
import com.example.ilan.myfinalproject.Extra.TrackerManager;
import com.example.ilan.myfinalproject.Extra.PlaceMenu;
import com.example.ilan.myfinalproject.Extra.CustomAlert;
import com.example.ilan.myfinalproject.Fragments.DetailsFragment;
import com.example.ilan.myfinalproject.Fragments.FavouritesFragment;
import com.example.ilan.myfinalproject.Fragments.MapFragment;
import com.example.ilan.myfinalproject.Fragments.SearchFragment;
import com.example.ilan.myfinalproject.Others.AppData;
import com.example.ilan.myfinalproject.Others.Place;
import com.example.ilan.myfinalproject.Others.Callback.PlaceDetails;
import com.example.ilan.myfinalproject.Others.Callback.PlaceList;
import com.example.ilan.myfinalproject.R;

import java.util.List;

//Here we go...

public class MainActivity extends ParrentActivity implements PlaceList, PlaceDetails, PlaceMenu.Callback {
    private static final int SETTINGS_REQUEST = 1;

    private ConnectivityManager connectivityManager;
    private FavouritesFragment favouritesFragment;
    private FragmentsAdapter fragmentsAdapter;
    private FragmentManager fragmentManager;
    private DetailsFragment detailsFragment;
    private DatabaseManager databaseManager;
    private SearchFragment searchFragment;
    private int searchFragmentPosition;
    private MapFragment mapFragment;
    private SearchView searchView;
    private PlaceMenu placeMenu;
    private ViewPager viewPager;
    private boolean isLandscape;
    private long lastPlaceId;


    @Override
    protected void onCreate() {
        lastPlaceId = AppData.NULL_DATA;

        placeMenu = new PlaceMenu(this);

        databaseManager = DatabaseManager.getInstance(this);

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    // called whenfragment is created/loaded
    @Override
    protected void onCreateFragments() {
        List<Fragment> fragments;

        fragmentManager = getSupportFragmentManager();

        fragments = fragmentManager.getFragments();

        if (fragments != null) {

            for (Fragment fragment : fragments) {

                if (fragment instanceof SearchFragment) {
                    searchFragment = (SearchFragment) fragment;
                    continue;
                }

                if (fragment instanceof FavouritesFragment) {
                    favouritesFragment = (FavouritesFragment) fragment;
                    continue;
                }

                if (isLandscape) {
                    if (fragment instanceof MapFragment) {
                        mapFragment = (MapFragment) fragment;
                        continue;
                    }

                    if (fragment instanceof DetailsFragment) {
                        detailsFragment = (DetailsFragment) fragment;
                        continue;
                    }
                }

            }
        }

        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance();
        }

        if (favouritesFragment == null) {
            favouritesFragment = FavouritesFragment.newInstance();
        }

        if (isLandscape && mapFragment == null) {
            mapFragment = MapFragment.newInstance();
        }

        fragmentsAdapter = new FragmentsAdapter(this, fragmentManager, searchFragment, favouritesFragment);

        searchFragmentPosition = fragmentsAdapter.getItemPosition(searchFragment);
    }

    // called when fragment view is created/loaded
    @Override
    protected void onCreateView() {
        TabLayout tabLayout;

        setContentView(R.layout.activity_main);
        // setu toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // setup viewpage adapter for fragemnts
        viewPager = (ViewPager) findViewById(R.id.ViewPager);
        viewPager.setAdapter(fragmentsAdapter);

        if (isLandscape) {

            fragmentManager.beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        } else {
            Button current = (Button) findViewById(R.id.current);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSearch(null);
                }
            });
        }
        // steup tablauout
        // TABS
        tabLayout = (TabLayout) findViewById(R.id.tl_tabs);
        tabLayout.setupWithViewPager(viewPager);

        ViewCompat.setLayoutDirection(tabLayout, ViewCompat.LAYOUT_DIRECTION_LTR);
    }

    // setup the option on the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem search, searchNearMe;

        getMenuInflater().inflate(R.menu.home_menu, menu);

        search = menu.findItem(R.id.m_search);
        search.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }


            @Override
            public boolean onQueryTextSubmit(String query) {

                onSearch(query);
                return true;
            }

        });

        searchNearMe = menu.findItem(R.id.m_search_nearme);

        if (isLandscape) {
            searchNearMe.setVisible(true);
            searchNearMe.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return true;
    }

    // setup  actions for the option on the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        boolean isActionDone = true;

        switch (item.getItemId()) {
            case R.id.m_clear_history://Clear history from searchmenu
                databaseManager.deleteLastSearch();
                searchFragment.clear();
                break;

            case R.id.m_setting:// Open setting activity
                intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST);
                break;

            case R.id.m_search_nearme: // Search near my radius
                onSearch(null);
                break;

            case R.id.m_exit:
                finish();//closes current activity and its associated fragments
                break;

            default:
                isActionDone = false;
        }

        return isActionDone;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (detailsFragment != null) {
            detailsFragment = null;

            mapFragment.removePlaceMarker();
        }
    }

    // when this specific activity goes to resume state
    @Override
    protected void onResume() {
        super.onResume();

        if (lastPlaceId != AppData.NULL_DATA) {
            onPlaceChanged(lastPlaceId);

            lastPlaceId = AppData.NULL_DATA;
        }
    }

    // called of place change not a interface
    private void onPlaceChanged(long placeId) {
        favouritesFragment.onPlaceChanged(placeId);
        searchFragment.onPlaceChanged(placeId);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean isFavouritesRemoved, isDistanceTypeChanged;

        if (resultCode == RESULT_OK) {

            if (requestCode == SETTINGS_REQUEST) {
                isFavouritesRemoved = data.getBooleanExtra(SettingActivity.KEY_FAVOURITES_REMOVED, false);
                isDistanceTypeChanged = data.getBooleanExtra(SettingActivity.KEY_DISTANCE_TYPE_CHANGED, false);

                if (isFavouritesRemoved) {
                    searchFragment.onFavouritesRemoved();
                    favouritesFragment.onFavouritesRemoved();

                } else if (isDistanceTypeChanged) {
                    searchFragment.refresh();
                    favouritesFragment.refresh();
                }
            }
        }

    }


    public void onSearch(final String keyword) {
        TrackerManager trackerManager;
        Location location;

        trackerManager = getLocationTrackerManager(new Runnable() {
            @Override
            public void run() {
                onSearch(keyword);
            }
        });

        if (trackerManager != null) //has permissions
        {

            if (trackerManager.isLocationEnabled()) {
                location = trackerManager.getCurrentLocation();

                if (location != null) {

                    if (isOnline()) {
                        searchFragment.onSearch(keyword, location.getLatitude(), location.getLongitude());
                    } else {
                        CustomAlert.createAlertDialog(this, getString(R.string.network_connection), getString(R.string.network_connection_msg), getString(R.string.settings), getString(R.string.cancel), new CustomAlert.AlertDialogListener() {
                            @Override
                            public void onPositive(DialogInterface dialog) {
                                startActivity(new Intent(Settings.ACTION_SETTINGS));
                            }


                            @Override
                            public void onNegative(DialogInterface dialog) {

                            }
                        });


                    }

                } else {
                    searchFragment.onLocationNotFound();
                    Log.d("Searching...", "Check Your Location");
                }
            } else {
                CustomAlert.createAlertDialog(this, getString(R.string.enable_location), getString(R.string.enable_location_msg), getString(R.string.settings), getString(R.string.cancel), new CustomAlert.AlertDialogListener() {
                    @Override
                    public void onPositive(DialogInterface dialog) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }


                    @Override
                    public void onNegative(DialogInterface dialog) {

                    }
                });

                Log.d("Searching....", "Not Enable");
            }
        } else {
            Log.d("Searching...", "Check Your Permissions");
        }

        viewPager.setCurrentItem(searchFragmentPosition, true);
    }


    public boolean isOnline() {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void onLocationChanged(Location location) {
        searchFragment.onLocationChanged(location);
        favouritesFragment.onLocationChanged(location);

        if (isLandscape) {
            mapFragment.onLocationChanged(location);

            if (detailsFragment != null) {
                detailsFragment.onLocationChanged(location);
            }
        }
    }
    // current location
    @Override
    public Location getCurrentLocation() {
        return getLocation();
    }

    // place clicked in the list
    @Override
    public void onClickPlace(Place place) {

        if (isLandscape) {
            detailsFragment = DetailsFragment.newInstance(place.getId());

            fragmentManager.beginTransaction()
                    .replace(R.id.container, detailsFragment)
                    .addToBackStack("details")
                    .commit();

            mapFragment.setPlaceMarker(place.getId());

            placeMenu.startActionMode(place);
        } else {
            lastPlaceId = place.getId();

            DetailsActivity.openActivity(this, lastPlaceId);
        }
    }

    // removed from favourates
    @Override
    public void onRemoveFavouritePlace(Fragment fragment, long placeId) {
        if (fragment == favouritesFragment) {
            searchFragment.onFavouritePlaceRemoved(placeId);

        } else if (fragment == searchFragment) {
            favouritesFragment.onFavouritePlaceRemoved(placeId);
        }
    }

    // added to favourates
    @Override
    public void onAddFavouritePlace(Place place) {
        favouritesFragment.onFavouritePlaceAdded(place);
    }

    // place selected
    @Override
    public void onClickDetail() {
        if (mapFragment != null) {
            mapFragment.animatePlaceMarker();
        }
    }


    @Override
    public void onStartPlaceActionMode(ActionMode.Callback callback) {
        startSupportActionMode(callback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }


    @Override
    public void onFavouriteToggleChanged(long placeId, boolean isFavouritePlace) {
        databaseManager.updatePlace(placeId, isFavouritePlace);

        onPlaceChanged(placeId);
    }


    @Override
    public void onShareSelected(Place place) {
        AppData.sharePlace(this, place);
    }


    @Override
    public void onActionModeClosed() {
        onBackPressed();
    }
}
