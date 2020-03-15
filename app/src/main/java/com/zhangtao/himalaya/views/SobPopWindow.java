package com.zhangtao.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhangtao.himalaya.R;
import com.zhangtao.himalaya.adapters.PlayListAdapter;
import com.zhangtao.himalaya.base.BaseApplication;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private TextView mCloseBtn;
    private final View mPopView;
    private RecyclerView mTrackList;
    private  final String TAG = "SobPopWindow";
    private PlayListAdapter mPlayListAdapter;
    private int mPosition = 0;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayListActionListener mPlayModeClickListener = null;
    private View mOrderBtnContainer;
    private ImageView mPlayOrderIv;
    private TextView mPlayOrderTv;

    public SobPopWindow(){
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //先要设置背景为透明，外部才可获取点击事件
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置外部可点击
        setOutsideTouchable(true);
        //加载View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pllyer_list, null, false);
        //设置内容
        setContentView(mPopView);
        //设置弹出动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }



    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.cancel_btn);
        mTrackList = mPopView.findViewById(R.id.play_list_iv);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(linearLayoutManager);
        //设置布局管理器
        mPlayListAdapter = new PlayListAdapter();
        mTrackList.setAdapter(mPlayListAdapter);
        //播放模式相关
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        //播放顺序相关
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_play_order_container);
        mPlayOrderTv = mPopView.findViewById(R.id.play_list_play_order_tv);
        mPlayOrderIv = mPopView.findViewById(R.id.play_list_play_order_iv);

    }

    private void initEvent() {
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();

            }
        });
        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }
            }
        });
        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2020/3/9 切换播放顺序
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onOrderClick();
                }
            }
        });
    }

    /**
     * 为适配器设置数据
     * @param data
     */
    public void setListData(List<Track> data){
        mPlayListAdapter.setData(data);
    }

    public void setCurrentPlayPosition(int position){
        mPosition = position;
        if(mPlayListAdapter != null){
            mPlayListAdapter.setCurrentPlayPosition(mPosition);
            mTrackList.scrollToPosition(mPosition);
        }
    }
    public void srcollToPosition(){
        mTrackList.scrollToPosition( mPosition);
    }

    public void setPlayListItemClickListener(PlayListAdapter.PlayListItemClickListener listener){
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    public void setPlayListActionListener(PlayListActionListener listener){
        mPlayModeClickListener = listener;
    }

    //更换模式切换按钮的UI
    public void updatePlayModeBtnUI(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_playmode_list_loop;
        String[] modeName = BaseApplication.getAppContext().getResources().getStringArray(R.array.paly_mode_name);
        String currentModeName = modeName[0];
        switch (playMode){
            case PLAY_MODEL_LIST:
                resId =  R.drawable.selector_playmode_sort_desc;
                currentModeName = modeName[0];
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId =  R.drawable.selector_playmode_single_loop;
                currentModeName = modeName[2];
                break;
            case PLAY_MODEL_RANDOM:
                resId =  R.drawable.selector_playmode_random;
                currentModeName = modeName[1];
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId =  R.drawable.selector_playmode_list_loop;
                currentModeName = modeName[3];
                break;
        }
        if ( mPlayModeIv != null) {
            mPlayModeIv.setImageResource(resId);
        }
        if (mPlayModeTv != null) {
            mPlayModeTv.setText(currentModeName);
        }
    }

    //更换模式切换按钮的UI
    public void updatePlayOrderBtnUI(boolean isRevers){
        if(!isRevers){
            mPlayOrderIv.setImageResource(R.drawable.selector_play_order_sort_desc);
            mPlayOrderTv.setText("正序");
        }else{
            mPlayOrderIv.setImageResource(R.drawable.selector_play_order_sort_asc);
            mPlayOrderTv.setText("逆序");
        }
    }

    public interface PlayListActionListener {
        //切换播放模式
        void onPlayModeClick();
        //切换播放顺序
        void onOrderClick();

    }

}
