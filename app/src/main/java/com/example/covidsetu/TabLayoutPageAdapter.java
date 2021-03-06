package com.example.covidsetu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabLayoutPageAdapter extends FragmentPagerAdapter {

    int tabCount;

    public TabLayoutPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : return new VaccinationFragment();
            case 1 : return new FragmentItem2();
            case 2 : return new FragmentItem3();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
