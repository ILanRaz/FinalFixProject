package com.example.ilan.myfinalproject.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Others.Callback.PlaceList;
import com.example.ilan.myfinalproject.Adatpers.PlacesAdapter;
import com.example.ilan.myfinalproject.Others.AppData;
import com.example.ilan.myfinalproject.DTBase.DatabaseManager;
import com.example.ilan.myfinalproject.Others.Place;
import com.example.ilan.myfinalproject.SearchManager.SearchService;
import com.example.ilan.myfinalproject.Design.Recyclerview.CustomRecyclerView;


public class SearchFragment extends BasicFragment implements CustomRecyclerView.OnItemClickListener, CustomRecyclerView.OnItemLongClickListener
{
private static final int TITLE_RES=R.string.title_tab_search;
private static final String KEY_SEARCH_STATE="isSearching";

private PlaceList placeList;
private CustomRecyclerView recyclerView;
private DatabaseManager databaseManager;
private SearchReceiver searchReceiver;
private PlacesAdapter adapter;
private PopupMenu popupMenu;
private ProgressBar loading;
private TextView msg;


    public SearchFragment()
    {
    // Required empty public constructor
    }


    public static SearchFragment newInstance()
    {
    SearchFragment fragment=new SearchFragment();
    return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);

    Location location;

    databaseManager=DatabaseManager.getInstance(getContext());

    popupMenu=null;

    adapter=new PlacesAdapter(getContext(), R.layout.search_place_item);
    adapter.setPlaces(databaseManager.getLastSearch());

    location=placeList.getCurrentLocation();

    if(location!=null)
        {
        adapter.updateDistance(location.getLatitude(),location.getLongitude());
        }

    searchReceiver=new SearchReceiver();

    LocalBroadcastManager.getInstance(getContext()).registerReceiver(searchReceiver,new IntentFilter(SearchService.ACTION_SEARCH));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    // Inflate the layout for this fragment
    View view=inflater.inflate(R.layout.fragment_list, container, false);

    view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            onHideMessage();
            }
        });

    recyclerView=(CustomRecyclerView) view.findViewById(R.id.RecyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);
    recyclerView.setOnItemClickListener(this);
    recyclerView.setOnItemLongClickListener(this);

    loading=(ProgressBar) view.findViewById(R.id.pb_loading);

    msg=(TextView) view.findViewById(R.id.tv_message);

    return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
    super.onActivityCreated(savedInstanceState);
    boolean isSearching;

    if (savedInstanceState != null)
        {
        isSearching=savedInstanceState.getBoolean(KEY_SEARCH_STATE);

        if(isSearching)
            {
            adapter.clearPlaces();
            loading.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    super.onSaveInstanceState(outState);

    outState.putBoolean(KEY_SEARCH_STATE, loading.getVisibility() == View.VISIBLE);
    }


    @Override
    public void onAttach(Context context)
    {
    super.onAttach(context);

    if(context instanceof PlaceList)
        {
        placeList=(PlaceList) context;
        }else
            {
            throw new RuntimeException(context.toString()+" must implement PlaceListCallback");
            }
    }


    @Override
    public void onDetach()
    {
    super.onDetach();

    placeList=null;
    }


    @Override
    public void onDestroy()
    {
    super.onDestroy();
    LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(searchReceiver);
    }


    public void onSearch(String keyword, double lat, double lng)
    {

    if(loading.getVisibility() == View.GONE)
        {
        onHideMessage();

        adapter.clearPlaces();

        loading.setVisibility(View.VISIBLE);

        SearchService.startActionSearch(getContext(), keyword, lat, lng);
        }
    }


    public void refresh()
    {
    if(isCreated())
        {
        adapter.refresh();
        }
    }


    public void onFavouritesRemoved()
    {
    if(isCreated())
        {
        adapter.unfavouriteAllPlaces();
        }
    }


    @Override
    public void onLocationChanged(Location location)
    {
    if(location!=null && isCreated())
        {
        adapter.updateDistance(location.getLatitude(),location.getLongitude());
        adapter.refresh();
        }
    }


    public void onLocationNotFound()
    {
    showMessage(getString(R.string.location_error_1) + "\n" + getString(R.string.location_error_2));
    }


    private void showMessage(String message)
    {
    msg.setText(message);
    msg.setVisibility(View.VISIBLE);
    loading.setVisibility(View.GONE);
    recyclerView.setVisibility(View.GONE);
    }


    private void onHideMessage()
    {
    if(msg.getVisibility() == View.VISIBLE)
        {
        msg.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, int position, int viewType)
    {
    openPlaceDetailsActivity(position);
    }


    @Override
    public void onItemLongClick(final RecyclerView.ViewHolder viewHolder, final int position, int viewType)
    {

    if(popupMenu==null)
        {
        popupMenu= new PopupMenu(getContext(), viewHolder.itemView);
        popupMenu.inflate(R.menu.menu_items);

        if(adapter.getItem(position).isFavourite())
            {
            popupMenu.getMenu().findItem(R.id.p_remove_favourites).setVisible(true);
            }else
                {
                popupMenu.getMenu().findItem(R.id.p_add_favourites).setVisible(true);
                }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                switch(item.getItemId())
                    {
                    case R.id.p_open:
                    openPlaceDetailsActivity(position);
                    break;

                    case R.id.p_share:
                    AppData.sharePlace(getContext(), adapter.getItem(position));
                    break;

                    case R.id.p_add_favourites:
                    updateFavourite(position);
                    break;

                    case R.id.p_remove_favourites:
                    updateFavourite(position);
                    break;

                    }

                return true;
                }
            });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
            {
                @Override
                public void onDismiss(PopupMenu menu)
                {
                popupMenu=null;
                }
            });

        popupMenu.show();
        }
    }


    private void updateFavourite(int position)
    {
    boolean isFavoritePlace;
    Place place;

    place=adapter.getItem(position);

    isFavoritePlace=!place.isFavourite();

    databaseManager.updatePlaceWithDelete(place.getId(), isFavoritePlace );


    if(isFavoritePlace)
        {
        placeList.onAddFavouritePlace(new Place(place));
        }else
            {
            placeList.onRemoveFavouritePlace(this, place.getId());
            }

    place.setFavourite(isFavoritePlace);
    adapter.notifyItemChanged(position);
    }


    public void onPlaceChanged(long placeId)
    {
    boolean isFavouritePlace;
    int position;
    Place place;

    position=adapter.getItemPosition(placeId);
    place=adapter.getItem(position);


    if(place!=null)
        {
        isFavouritePlace=databaseManager.isFavouritePlace(placeId);

        place.setFavourite(isFavouritePlace);
        adapter.notifyItemChanged(position);
        }
    }


    public void clear()
    {
    if(isCreated())
        {
        adapter.clearPlaces();
        }
    }


    public void onFavouritePlaceRemoved(long placeId)
    {
    int position;
    Place place;

    position=adapter.getItemPosition(placeId);

    place=adapter.getItem(position);

    if(place!=null)
        {
        place.setFavourite(false);
        adapter.notifyItemChanged(position);
        }
    }


    private void openPlaceDetailsActivity(int position)
    {
    Place place;

    place=adapter.getItem(position);

    placeList.onClickPlace(place);
    }


    @Override
    public int getTitleRes()
    {
    return TITLE_RES;
    }


    private class SearchReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
        String message;
        int status;

        message=null;

        status=intent.getIntExtra(SearchService.EXTRA_STATUS, SearchService.STATUS_ERROR);

        switch(status)
            {
            case SearchService.STATUS_OK:
            adapter.setPlaces(databaseManager.getLastSearch());
            onLocationChanged(placeList.getCurrentLocation());
            break;

            case SearchService.STATUS_ZERO_RESULTS:
            message=getString(R.string.no_results);
            break;

            case SearchService.STATUS_OVER_QUERY_LIMIT:
            message=getString(R.string.search_locked_1) + "\n" + getString(R.string.search_locked_2);
            break;

            case SearchService.STATUS_ERROR:
            message=getString(R.string.search_failed);
            break;
            }

        if(message!=null)
            {
            showMessage(message);
            }else
                {
                loading.setVisibility(View.GONE);
                }
        }
    }

}
