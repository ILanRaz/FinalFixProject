package com.example.ilan.myfinalproject.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;



public abstract class BasicFragment extends Fragment
{
private boolean isCreated;

abstract public int getTitleRes();
abstract public void onLocationChanged(Location location);


    public BasicFragment()
    {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);

    isCreated=false;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
    super.onViewCreated(view, savedInstanceState);

    isCreated=true;
    }


    @Override
    public void onDestroyView()
    {
    super.onDestroyView();

    isCreated=false;
    }


    public boolean isCreated()
    {
    return isCreated;
    }
}
