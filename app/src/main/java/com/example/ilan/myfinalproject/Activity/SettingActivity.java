package com.example.ilan.myfinalproject.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Others.AppData;
import com.example.ilan.myfinalproject.Fragments.SettingsFragment;


// settings of the app
public class SettingActivity extends AppCompatActivity {
    public static final String KEY_FAVOURITES_REMOVED = "isFavouritesRemoved";
    public static final String KEY_DISTANCE_TYPE_CHANGED = "isDistanceTypeChanged";

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppData.setAppLocale(this);

        FragmentManager fragmentManager;
        Fragment fragment;

        setContentView(R.layout.settings);

        ActionBar actionBar = getSupportActionBar();
        // set title of the toolbar
        actionBar.setTitle(R.string.title_activity_settings);
        // enable back button
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fragmentManager = getFragmentManager();
        fragment = fragmentManager.findFragmentByTag(SettingsFragment.TAG);

        if (fragment != null && fragment instanceof SettingsFragment) {
            settingsFragment = (SettingsFragment) fragment;
        } else {
            settingsFragment = SettingsFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, settingsFragment, SettingsFragment.TAG)
                    .commit();
        }
    }

//Menu bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        Intent resultData = new Intent();
        resultData.putExtra(KEY_FAVOURITES_REMOVED, settingsFragment.isFavouritesRemoved());
        resultData.putExtra(KEY_DISTANCE_TYPE_CHANGED, settingsFragment.isDistanceTypeChanged());

        setResult(Activity.RESULT_OK, resultData);

        super.onBackPressed();
    }
}
