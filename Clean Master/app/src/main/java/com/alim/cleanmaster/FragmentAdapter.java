package com.alim.cleanmaster;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FragmentAdapter  extends FragmentPagerAdapter {
    ArrayList<Fragment>  fragments = new ArrayList<>();
    ArrayList<String>  fragmentsNames = new ArrayList<>();

    public FragmentAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    public void addFragment(Fragment fragment , String name){
        fragments.add(fragment);
        fragmentsNames.add(name);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentsNames.get(position);
    }
}
