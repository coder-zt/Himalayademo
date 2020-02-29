package com.zhangtao.himalaya.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.zhangtao.himalaya.utils.FragmentCreateor;

import static com.zhangtao.himalaya.utils.FragmentCreateor.PAGE_COUNT;

public class MainContentAdapter extends FragmentPagerAdapter {
    public MainContentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentCreateor.getFragment(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}

