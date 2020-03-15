package com.zhangtao.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhangtao.himalaya.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailLisrAdatper extends RecyclerView.Adapter<DetailLisrAdatper.InnerHolder> {

    List<Track> mDetailData = new ArrayList<>();
    //日期格式
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        holder.itemView.setTag(position);
        holder.setData(mDetailData.get(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null){
                    mItemClickListener.onItemClick( mDetailData,position);
                }
                Toast.makeText(v.getContext(), "你点击了第" + ((int)v.getTag() + 1 + "") + "个item", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> track) {
        //清除原来的数据
        mDetailData.clear();
        //添加新的数据
        mDetailData.addAll(track);
        //更新UI
        notifyDataSetChanged();
    }
    public void setmItemClickListener(ItemClickListener listener){
        mItemClickListener = listener;
    }

    public interface ItemClickListener{
        void onItemClick(List<Track> detailData, int position);
    }

    class InnerHolder extends RecyclerView.ViewHolder {
        InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setData(Track track, int position) {
            //曲目标题
            TextView tvTitle = itemView.findViewById(R.id.tv_title);
            //曲目序号
            TextView tvIndex = itemView.findViewById(R.id.order_text);
            //曲目播放量
            TextView tvCount = itemView.findViewById(R.id.tv_play_info);
            //曲目播放时长
            TextView tvTime = itemView.findViewById(R.id.tv_play_time);
            //曲目更新时间
            TextView tvUpdateTime = itemView.findViewById(R.id.updated_at);
            tvTitle.setText(track.getTrackTitle());
            tvIndex.setText(position + 1 + "");
            String countString = "";
            countString = track.getPlayCount() + "";
            if(track.getPlayCount() > 10000){
                countString = String.format("%.1f万",track.getPlayCount()/10000.0);
            }
            tvCount.setText(countString);
            tvTime.setText(timeFormat.format(track.getDuration() * 1000));
            tvUpdateTime.setText(dateFormat.format(track.getUpdatedAt()));
        }
    }


}
