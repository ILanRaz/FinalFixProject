package com.example.ilan.myfinalproject.Fragments;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.DTBase.DatabaseManager;
import com.example.ilan.myfinalproject.Extra.PermissionManager;
import com.example.ilan.myfinalproject.Others.Place;


public class MapFragment extends BasicFragment implements OnMapReadyCallback
{
private static final int TITLE_RES=R.string.title_tab_map;

private static final int DEFAULT_ZOOM=18;

private static final String ARG_PLACE_ID="placeId";

private FloatingActionButton myLocationButton;
private LatLng placeLocation,myLocation;
private DatabaseManager databaseManager;
private GoogleMap googleMap;
private Marker placeMarker;
private String placeName;
private MapView mapView;


    public MapFragment()
    {

    }


    public static MapFragment newInstance(long placeId)
    {
    MapFragment fragment;
    Bundle args;

    args=new Bundle();

    args.putLong(ARG_PLACE_ID, placeId);

    fragment=new MapFragment();
    fragment.setArguments(args);

    return fragment;
    }


    public static MapFragment newInstance()
    {
    MapFragment fragment=new MapFragment();

    return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);

    Bundle args;

    databaseManager=DatabaseManager.getInstance(getContext());

    googleMap=null;
    myLocation=null;

    args=getArguments();

    if(args!=null)
        {
        setPlaceMarker(args.getLong(ARG_PLACE_ID));
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
    View view=inflater.inflate(R.layout.fragment_map, container, false);

    mapView = (MapView) view.findViewById(R.id.mapview);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);

    myLocationButton=(FloatingActionButton) view.findViewById(R.id.ib_my_location);
    myLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            animateMyLocation();
            }
        });


    if(myLocation==null)
        {
        myLocationButton.setVisibility(View.GONE);
        }

    return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
    this.googleMap=googleMap;

    if(placeLocation!=null)
        {
        createPlaceMarker();
        }else
            {
            animateMyLocation();
            }

    if(PermissionManager.hasPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
        {
        googleMap.setMyLocationEnabled(true);
        }

    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }


    private void createPlaceMarker()
    {

    if(placeMarker==null)
        {
        placeMarker=googleMap.addMarker(new MarkerOptions().position(placeLocation).title(placeName));
        }else
            {
            placeMarker.setPosition(placeLocation);
            placeMarker.setTitle(placeName);
            }

    animatePlaceMarker();
    }


    public void setPlaceMarker(long placeId)
    {
    Place place=databaseManager.getPlace(placeId);

    placeName=place.getName();

    placeLocation=new LatLng(place.getLat(), place.getLng());

    if(googleMap!=null)
        {
        createPlaceMarker();
        }
    }


    public void removePlaceMarker()
    {

    if(placeMarker!=null)
        {
        placeMarker.remove();

        placeName=null;
        placeMarker=null;
        placeLocation=null;
        }
    }


    public void animatePlaceMarker()
    {

    if(placeMarker!=null)
        {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, DEFAULT_ZOOM));
        }
    }


    private void animateMyLocation()
    {

    if(myLocation!=null && googleMap != null)
        {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_ZOOM));
        }
    }


    @Override
    public void onLocationChanged(Location location)
    {

    if(location != null)
        {
        myLocation=new LatLng(location.getLatitude(), location.getLongitude());

        if(isCreated())
            {
            myLocationButton.setVisibility(View.VISIBLE);

            if(placeLocation==null)
                {
                animateMyLocation();
                }
            }
        }
    }


    @Override
    public void onResume()
    {
    super.onResume();
    mapView.onResume();
    }


    @Override
    public void onPause()
    {
    super.onPause();
    mapView.onPause();
    }


    @Override
    public void onDestroy()
    {
    super.onDestroy();
    mapView.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onLowMemory()
    {
    super.onLowMemory();
    mapView.onLowMemory();
    }


    @Override
    public int getTitleRes()
    {
    return TITLE_RES;
    }

}
