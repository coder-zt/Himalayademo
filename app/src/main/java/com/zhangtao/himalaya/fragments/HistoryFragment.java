package com.zhangtao.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangtao.himalaya.R;
import com.zhangtao.himalaya.base.BaseFragment;

public class HistoryFragment extends BaseFragment {

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }
}
