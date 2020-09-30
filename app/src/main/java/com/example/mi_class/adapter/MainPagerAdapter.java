package com.example.mi_class.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final FragmentManager fragmentManager;
    private final ArrayList<Fragment> list;

    public MainPagerAdapter(FragmentManager fm, ArrayList<Fragment> l){
        super(fm);
        this.fragmentManager = fm;
        this.list = l;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
