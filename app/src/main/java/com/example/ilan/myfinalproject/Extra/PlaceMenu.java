package com.example.ilan.myfinalproject.Extra;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Others.Place;

// custom menu item for the toolbar
public class PlaceMenu implements ActionMode.Callback
{
private boolean isFavouritePlace;
private Callback callback;
private Place place;


    public PlaceMenu(Callback callback)
    {
    this.callback=callback;

    place=null;
    }


    public void startActionMode(Place place)
    {
    this.place=place;
    isFavouritePlace=place.isFavourite();

    callback.onStartPlaceActionMode(this);
    }


    public static void createPlaceMenu(Menu menu, MenuInflater inflater, boolean isFavouritePlace)
    {
    MenuItem favouriteToggle;

    inflater.inflate(R.menu.place_details, menu);

    menu.findItem(R.id.m_share).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

    favouriteToggle=menu.findItem(R.id.m_favourite_toggle);

    favouriteToggle.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

    if(isFavouritePlace)
        {
        favouriteToggle.setIcon(R.drawable.ic_menu_star);
        }else
            {
            favouriteToggle.setIcon(R.drawable.ic_menu_star_border);
            }
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
    createPlaceMenu(menu, mode.getMenuInflater(), isFavouritePlace);

    mode.setTitle(place.getName());

    return true;
    }


    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {

        return false;
    }


    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {

    switch (item.getItemId())
        {

        case R.id.m_favourite_toggle:
        isFavouritePlace=!isFavouritePlace;

        callback.onFavouriteToggleChanged(place.getId(), isFavouritePlace);

        if(isFavouritePlace)
            {
            item.setIcon(R.drawable.ic_menu_star);
            }else
                {
                item.setIcon(R.drawable.ic_menu_star_border);
                }
        break;

        case R.id.m_share:
        callback.onShareSelected(place);
        break;
        }

    return true;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
    place=null;

    callback.onActionModeClosed();
    }


    public interface Callback
    {
    void onStartPlaceActionMode(ActionMode.Callback callback);
    void onFavouriteToggleChanged(long placeId, boolean isFavouritePlace);
    void onShareSelected(Place place);
    void onActionModeClosed();
    }
}
