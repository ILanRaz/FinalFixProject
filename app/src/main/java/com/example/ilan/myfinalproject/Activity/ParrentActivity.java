package com.example.ilan.myfinalproject.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;

import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Others.AppData;
import com.example.ilan.myfinalproject.Extra.CustomAlert;
import com.example.ilan.myfinalproject.Extra.TrackerManager;
import com.example.ilan.myfinalproject.Extra.PermissionManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

// baee class for home and detail activity
// Common functions and variables
public abstract class ParrentActivity extends AppCompatActivity implements TrackerManager.OnLocationChangedListener {
    private static final int LOCATION_REQUEST_CODE = 0;

    private TrackerManager trackerManager;
    private Runnable lastAction;

    abstract protected void onCreate();

    abstract protected void onCreateView();

    abstract protected void onCreateFragments();

    // called when activity is created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppData.setAppLocale(this);
        // calling the abstract method
        onCreate();
        if (PermissionManager.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE)) {
            createLocationTrackerManager();
        }

        onCreateFragments();
        onCreateView();
    }


    public TrackerManager getLocationTrackerManager(Runnable action) {
        if (trackerManager == null) {
            requestLocationTrackerManager(action);
        }
        return trackerManager;
    }


    private void requestLocationTrackerManager(Runnable action) {
        lastAction = action;

        if (PermissionManager.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE)) {
            createLocationTrackerManager();
        }
    }


    @RequiresPermission(anyOf = {ACCESS_FINE_LOCATION})
    private void createLocationTrackerManager() {
        trackerManager = TrackerManager.getInstance(this);

        if (lastAction != null) {
            lastAction.run();
        }

        lastAction = null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createLocationTrackerManager();
                } else {
                    CustomAlert.createAlertDialog(this, getString(R.string.error), getString(R.string.no_location_access), getString(R.string.ok), null, null);
                }

                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (trackerManager != null) {
            trackerManager.registerLocationUpdates(this);
            onLocationChanged(trackerManager.getCurrentLocation());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // remove the location listener
        if (trackerManager != null) {
            trackerManager.removeLocationUpdates();
        }
    }


    public Location getLocation() {
        Location location = null;
        if (trackerManager != null) {
            location = trackerManager.getCurrentLocation();
        }
        return location;
    }

}
