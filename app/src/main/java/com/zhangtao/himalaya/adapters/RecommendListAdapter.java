package com.zhangtao.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.zhangtao.himalaya.R;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {
    private List<Album> mData = new ArrayList<>();
    private OnItemClickListener mItemClickListener;

    @NonNull
    @Override
    public RecommendListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载View
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendListAdapter.InnerHolder holder, int position) {
        //设置数据
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null){
                    int position = (int)v.getTag();
                    mItemClickListener.onItemClick(position, mData.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //返回显示项目个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albums) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albums);
        }
        //更新一下UI
        notifyDataSetChanged();
    }

     class InnerHolder extends RecyclerView.ViewHolder {
        InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setData(Album album) {
            //找到个控件设置数据
            //专辑封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //标题
            TextView albumTitleTv = itemView.findViewById(R.id.album_titlle_tv);
            //描述
            TextView albumDescTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContenCountTv = itemView.findViewById(R.id.album_content_size);
            albumTitleTv.setText(album.getAlbumTitle());
            albumDescTv.setText(album.getAlbumIntro());
            String countString = "";
            countString = album.getPlayCount() + "";
            if(album.getPlayCount() > 10000){
                countString = String.format("%.1f万",album.getPlayCount()/10000.0);
            }
            albumPlayCountTv.setText(countString);
            albumContenCountTv.setText(album.getIncludeTrackCount() + "");
            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int index, Album album);
    }
}
