package com.instify.android.ux.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.instify.android.ux.fragments.AttendanceFragment;
import com.instify.android.ux.fragments.NotesFragment;
import com.instify.android.ux.fragments.TimeTableFragment;

/**
 * Created by Chirag on 05-05-18.
 */

public class MePagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public MePagerAdapter(FragmentManager fm, int NumberOfTabs)
    {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position)
        {

            case 0:
                AttendanceFragment tab1 = new AttendanceFragment();
                return tab1;
            case 1:
                TimeTableFragment tab2 = new TimeTableFragment();
                return  tab2;
            case 2:
                NotesFragment tab3 = new NotesFragment();
                return  tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
