package com.zhangtao.himalaya.adapters;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhangtao.himalaya.R;
import com.zhangtao.himalaya.base.BaseApplication;
import com.zhangtao.himalaya.views.SobPopWindow;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {

    private List<Track> mData = new ArrayList<>();
    private int PlayingIndex = 0;
    private PlayListItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ListView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list,parent,false);
        return new InnerHolder(ListView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        ///设置数据
        Track track = mData.get(position);
        TextView trackTitle = holder.itemView.findViewById(R.id.track_title);
        trackTitle.setText(track.getTrackTitle());
        ImageView isPlayImg = holder.itemView.findViewById(R.id.is_play_img);
        isPlayImg.setVisibility(PlayingIndex == position?VISIBLE:View.GONE);
        if(PlayingIndex == position){
            trackTitle.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.main_color));
        }else{
            trackTitle.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.play_list_text_color));

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        //更新数据
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
            PlayingIndex = position;
            notifyDataSetChanged();
    }

    public void setOnItemClickListener(PlayListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface PlayListItemClickListener{
        void onItemClick(int position);
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
