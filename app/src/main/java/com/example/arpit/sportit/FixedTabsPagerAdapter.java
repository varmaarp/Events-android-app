package com.example.arpit.sportit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Arpit on 10-05-2017.
 */

public class FixedTabsPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public FixedTabsPagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return context.getString(R.string.category_attending);
            case 1:
                return context.getString(R.string.category_myEvents);
            case 2:
                return context.getString(R.string.category_viewAll);
            default:
                return null;
        }
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AttendingFragment();
            case 1:
                return new MyEventsFragment();
            case 2:
                return new ViewAllFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
