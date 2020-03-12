package com.example.james.bpm;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public MyFragmentPageAdapter(FragmentManager fragManager, List<Fragment> fragments){
        super(fragManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position){
        return this.fragments.get(position);
    }

    @Override
    public int getCount(){
        return this.fragments.size();
    }
}
