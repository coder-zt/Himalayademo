package com.zhangtao.himalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhangtao.himalaya.adapters.PlayListAdapter;
import com.zhangtao.himalaya.adapters.PlayerTrackPagerAdappter;
import com.zhangtao.himalaya.base.BaseActivity;
import com.zhangtao.himalaya.interfaces.IPlayerCallback;
import com.zhangtao.himalaya.presents.PlayerPresenter;
import com.zhangtao.himalaya.utils.LogUtil;
import com.zhangtao.himalaya.views.SobPopWindow;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {


    private static final String TAG = "PlayerActivity" ;
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm::ss");
    private int mCurrentProgress = 0;
    private TextView mTotalDurationTv;
    private TextView mCurrentPositionTv;
    private SeekBar mDurationBar;
    private boolean mIsUserToush = false;
    private ImageView mPlayNextIv;
    private ImageView mPlayPreIv;
    private TextView mTitleTv;
    private String mTrackTitle;
    private ViewPager mTrackViewPager;
    private PlayerTrackPagerAdappter mPlayerTrackPagerAdappter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayModeSwitchBtn;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sModeMap = new HashMap<>();
    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;
    //设计播放模式
    //PLAY_MODEL_SINGLE_LOOP,
    //PLAY_MODEL_LIST,
    //PLAY_MODEL_LIST_LOOP,
    //PLAY_MODEL_RANDOM;
    static {
        sModeMap.put(PLAY_MODEL_SINGLE_LOOP,PLAY_MODEL_LIST);
        sModeMap.put(PLAY_MODEL_LIST,PLAY_MODEL_LIST_LOOP);
        sModeMap.put(PLAY_MODEL_LIST_LOOP,PLAY_MODEL_RANDOM);
        sModeMap.put(PLAY_MODEL_RANDOM,PLAY_MODEL_SINGLE_LOOP);
    }

    private ImageView mPlayListBtn;
    private SobPopWindow mSobPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;
    private int mPlayIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        //在界面初始化后，再获取数据
        mPlayerPresenter.getPlayList();
        initEvent();
        initBgAnimation();
    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f, 0.7f);
        mEnterBgAnimator.setDuration(300);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                updateBgAlpha(value);
            }
        });
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f, 1.0f);
        mOutBgAnimator.setDuration(300);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                updateBgAlpha(value);
            }
        });
    }


    /**
     * 设置事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击播放/暂停按钮
                if(mPlayerPresenter.isPlaying()){
                    mPlayerPresenter.pause();
                }else{
                    mPlayerPresenter.play();
                }

            }
        });

        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserToush = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserToush = false;
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });
        mPlayNextIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一首
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });
        mPlayPreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放上一个
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });
        //ViewPager的事件设置
        mTrackViewPager.addOnPageChangeListener(this);
        mTrackViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                    break;
                }
                return false;
            }
        });
        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayMode();
            }
        });
        mSobPopWindow.setPlayListActionListener(new SobPopWindow.PlayListActionListener() {



            @Override
            public void onPlayModeClick() {
                // 切换模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //切换顺序
                //Toast.makeText(PlayerActivity.this,"切换顺序", Toast.LENGTH_SHORT ).show();
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }
            }
        });
        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mOutBgAnimator.start();
            }
        });
        mSobPopWindow.setPlayListItemClickListener(new PlayListAdapter.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //sobPopWindow中Item被点击
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PopWindow的显示位置
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM, 0 , 0);
                mSobPopWindow.srcollToPosition();
                //处理一下背景，有点透明度
                mEnterBgAnimator.start();
            }
        });

    }

    private void switchPlayMode() {
        XmPlayListControl.PlayMode playMode = sModeMap.get(mCurrentMode);
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_playmode_list_loop;
        switch (mCurrentMode){
            case PLAY_MODEL_LIST:
               resId =  R.drawable.selector_playmode_sort_desc;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId =  R.drawable.selector_playmode_single_loop;
                break;
            case PLAY_MODEL_RANDOM:
                resId =  R.drawable.selector_playmode_random;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId =  R.drawable.selector_playmode_list_loop;
                break;
        }
        if (mPlayModeSwitchBtn != null) {
            mPlayModeSwitchBtn.setImageResource(resId);
        }



    }

    /**
     * 置背景的透明度
     * @param alpha
     */
    public void updateBgAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    /**
     * 找到控件
     */
    private void initView() {
        mControlBtn = this.findViewById(R.id.iv_play_pause);
        mTotalDurationTv = this.findViewById(R.id.tv_total_time);
        mCurrentPositionTv = this.findViewById(R.id.tv_current_time);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayPreIv = this.findViewById(R.id.play_pre_icon);
        mPlayNextIv = this.findViewById(R.id.play_next_icon);
        mTitleTv = this.findViewById(R.id.track_title);
        if(!TextUtils.isEmpty(mTrackTitle)){
            mTitleTv.setText(mTrackTitle);
        }
        mTrackViewPager = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mPlayerTrackPagerAdappter = new PlayerTrackPagerAdappter();
        //设置适配器
        mTrackViewPager.setAdapter(mPlayerTrackPagerAdappter);
        //切换播放模式的按钮
        mPlayModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);
        //播放列表
        mPlayListBtn = this.findViewById(R.id.player_list_switch_btn);
        mSobPopWindow = new SobPopWindow();
    }

    @Override
    public void onPlayStart() {
        //开始播放，修改UI为暂停按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        //暂停播放，修改UI为暂停按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        //数据加载完成的回调
        if (mPlayerTrackPagerAdappter != null) {
            mPlayerTrackPagerAdappter.setData(list);
        }
        //设置播放列表的数据
        if (mSobPopWindow != null) {
            mSobPopWindow.setListData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式后的回调，更新UI
        mCurrentMode = playMode;
        mSobPopWindow.updatePlayModeBtnUI(playMode);
        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(int currentProgress, int total) {
        //更新播放进度，更新进度条
        //更新时间显示
        mDurationBar.setMax(total);
        String totalDuration;
        String currentPosition;
        if(total > 60 * 60 * 10000){
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentProgress);
        }else{
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentProgress);
        }
        if (mTotalDurationTv != null) {
            mTotalDurationTv.setText(totalDuration);
        }
        if (mCurrentPositionTv != null) {
            mCurrentPositionTv.setText(currentPosition);
        }
        //更新进度条

        if (!mIsUserToush) {
            mDurationBar.setProgress(currentProgress);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if(track != null) {
            mPlayIndex = playIndex;
            this.mTrackTitle = track.getTrackTitle();
            if (mTitleTv != null) {
                mTitleTv.setText(mTrackTitle);
            }
            //档节目改变的时候，我们获取当前节目的位置
            if (mTrackViewPager != null) {
                mTrackViewPager.setCurrentItem(playIndex);
            }
            //修改播放列表里的播放位置
            if (mSobPopWindow != null) {
                LogUtil.d(TAG, "设置位置为" + playIndex);
                mSobPopWindow.setCurrentPlayPosition(playIndex);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isRevers) {
        mSobPopWindow.updatePlayOrderBtnUI(isRevers);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayerPresenter != null){
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
        if (mSobPopWindow != null) {
            mSobPopWindow.dismiss();
        }
        LogUtil.d(TAG, "onDestroy。。。");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //当页面选中的时候，就去切换播放器播放的内容
        if(mPlayerPresenter != null && mIsUserSlidePager){
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
