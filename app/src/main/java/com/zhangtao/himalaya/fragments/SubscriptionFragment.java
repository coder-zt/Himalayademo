package com.zhangtao.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangtao.himalaya.R;
import com.zhangtao.himalaya.base.BaseFragment;

public class SubscriptionFragment extends BaseFragment {

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);
        return view;
    }
}
