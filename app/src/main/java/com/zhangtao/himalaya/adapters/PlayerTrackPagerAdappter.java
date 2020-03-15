package com.zhangtao.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhangtao.himalaya.R;

import java.util.ArrayList;
import java.util.List;

public class PlayerTrackPagerAdappter extends PagerAdapter {
    List<Track> mData = new ArrayList<>();
    @Override
    public int getCount() {
        return mData.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View viewItem = LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_pager, container, false);
        container.addView(viewItem);
        //设置数据
        //找到控件
        ImageView item = viewItem.findViewById(R.id.item_track_pager);
        //设置图片
        Track track = mData.get(position);
        String coverUrlLarge = track.getCoverUrlLarge();
        Picasso.with(container.getContext()).load(coverUrlLarge).into(item);
        return viewItem;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<Track> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }
}
