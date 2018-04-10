package com.example.ilan.myfinalproject.Adatpers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ilan.myfinalproject.Fragments.BasicFragment;


// This adapter loading the fragments to viewpager
public class FragmentsAdapter extends FragmentPagerAdapter {
    private BasicFragment[] fragments;
    private Context context;


    public FragmentsAdapter(Context context, FragmentManager fm, BasicFragment... fragments) {
        super(fm);

        this.context = context;
        this.fragments = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }


    @Override
    public int getCount() {
        return fragments.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(fragments[position].getTitleRes());
    }


    public int getItemPosition(BasicFragment fragment) {
        int itemPosition = POSITION_NONE;

        for (int i = 0; i < fragments.length; i++) {

            if (fragment == fragments[i]) {
                itemPosition = i;
                break;
            }
        }

        return itemPosition;
    }

}
