package com.example.ilan.myfinalproject.Extra;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
// tracking using gps
public class TrackerManager
{
private static final long MIN_DISTANCE_UPDATE=100; // 100 meters
private static final long MIN_TIME_UPDATE=1000*10; // 10 sec
private static final long TIME_GPS_FIX_LOST=MIN_TIME_UPDATE + MIN_TIME_UPDATE / 2; // 15 sec
private static final long GPS_TIMEOUT=1000*60*3; // 3 minutes

private static TrackerManager instance;

private boolean hasGPSProvider,hasNetworkProvider,isGPSEnabled,isNetworkEnabled,isGpsFix,isRegistered;
private OnLocationChangedListener locationChangedListener;
private LocationListener gpsListener,networkListener;
private LocationManager locationManager;
private Runnable removeGpsUpdatesAction;
private Location currentLocation;
private Handler handler;


    @RequiresPermission(anyOf = {ACCESS_FINE_LOCATION})
    public synchronized static TrackerManager getInstance(Context context)
    {

    if(instance==null)
        {
        instance=new TrackerManager((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        }

    return instance;
    }


    private TrackerManager(LocationManager locationManager)
    {
    Location bestLocation,gpsLocation,networkLocation;
    List<String> providers;

    this.locationManager=locationManager;

    currentLocation=null;

    isGpsFix=false;
    isRegistered=false;

    handler=new Handler();

    providers=locationManager.getAllProviders();

    hasGPSProvider=providers.contains(LocationManager.GPS_PROVIDER);
    hasNetworkProvider=providers.contains(LocationManager.NETWORK_PROVIDER);

    updateLocationState();

    if(isGPSEnabled || isNetworkEnabled)
        {
        gpsLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        networkLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(gpsLocation!=null && networkLocation !=null)
            {
            bestLocation= gpsLocation.getTime() > networkLocation.getTime() ? gpsLocation : networkLocation;
            }else
                {
                bestLocation= gpsLocation!=null ? gpsLocation : networkLocation;
                }

        if(currentLocation == null)
            {
            currentLocation=bestLocation;
            }

        }

    gpsListener=new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
            updateLocation(location);
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            Log.d("LTM-onStatusChanged","provider= "+ provider+" status= "+status);
            }


            @Override
            public void onProviderEnabled(String provider)
            {
            isGPSEnabled=true;
            }


            @Override
            public void onProviderDisabled(String provider)
            {
            isGPSEnabled=false;
            }
        };


    networkListener=new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
            updateLocation(location);
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            Log.d("LTM-onStatusChanged","provider= "+ provider+" status= "+status);
            }


            @Override
            public void onProviderEnabled(String provider)
            {
            isNetworkEnabled=true;
            }


            @Override
            public void onProviderDisabled(String provider)
            {
            isNetworkEnabled=false;
            }
        };

    removeGpsUpdatesAction=new Runnable()
        {
            @Override
            public void run()
            {

            if(!isGpsFix)
                {

                removeGPSUpdates();

                gpsListener=null;

                Log.d("LTM-onLocationChanged","gps updates removed");
                }
            }
        };

    handler.postDelayed(removeGpsUpdatesAction, GPS_TIMEOUT);

    registerLocationUpdates(null);
    }


    private void updateLocationState()
    {
    isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void registerLocationUpdates(OnLocationChangedListener locationChangedListener)
    {
    this.locationChangedListener=locationChangedListener;

    if(!isRegistered)
        {

        updateLocationState();

        if(hasGPSProvider && gpsListener!=null)
            {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, gpsListener);
            }

        if(hasNetworkProvider)
            {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, networkListener);
            }

        isRegistered=true;
        }
    }


    public void removeLocationUpdates()
    {
    locationChangedListener=null;

    removeGPSUpdates();

    locationManager.removeUpdates(networkListener);

    isRegistered=false;
    }


    private void removeGPSUpdates()
    {
    if(gpsListener!=null)
        {
        locationManager.removeUpdates(gpsListener);
        }
    }


    public void updateLocation(Location location)
    {
    boolean isGpsLocation;

    if(location == null)
        {
        return;
        }

    if(currentLocation!=null)
        {
         Log.d("LTM","updateLocation left time = " + (location.getTime() - currentLocation.getTime()) /1000 );
        }


    isGpsLocation=location.getProvider().equals(LocationManager.GPS_PROVIDER);

    if(isGpsLocation)
        {
        isGpsFix=true;
        handler.removeCallbacks(removeGpsUpdatesAction);
        Log.d("LTM-onLocationChanged","gps fix");
        }else if(isGpsFix && ( (location.getTime() - currentLocation.getTime()) > TIME_GPS_FIX_LOST))
            {
            isGpsFix=false;
            handler.postDelayed(removeGpsUpdatesAction, GPS_TIMEOUT);
            Log.d("LTM-onLocationChanged","lost gps");
            }

    if(currentLocation == null || isGpsLocation || !isGpsFix )
        {
        currentLocation=location;

        if(locationChangedListener != null)
            {
            locationChangedListener.onLocationChanged(currentLocation);
            }
        //Log.d("LTM-onLocationChanged","location provider="+ location.getProvider() + "  time="+ DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date(location.getTime())));
        }

    }


    public Location getCurrentLocation()
    {
    return currentLocation;
    }


    public boolean isLocationEnabled()
    {
    return isGPSEnabled || isNetworkEnabled;
    }


    public interface OnLocationChangedListener
    {
       void onLocationChanged(Location location);
    }
}
