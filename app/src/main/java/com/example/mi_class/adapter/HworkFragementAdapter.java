package com.example.mi_class.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.mi_class.domain.StuLogInfo;

import java.util.List;

public class HworkFragementAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragments;
    List<String> titles;
    public HworkFragementAdapter(FragmentManager fragmentManager, List<Fragment> fragments, List<String> titles){
        super(fragmentManager);
        this.fragments = fragments;
        this.titles =titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
