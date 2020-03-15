package com.zhangtao.himalaya;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhangtao.himalaya.adapters.IndicatorAdapter;
import com.zhangtao.himalaya.adapters.MainContentAdapter;
import com.zhangtao.himalaya.base.BaseActivity;
import com.zhangtao.himalaya.interfaces.IPlayerCallback;
import com.zhangtao.himalaya.presents.PlayerPresenter;
import com.zhangtao.himalaya.presents.RecommendPresenter;
import com.zhangtao.himalaya.utils.LogUtil;
import com.zhangtao.himalaya.views.RoundRectIamgeView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends BaseActivity implements IPlayerCallback {

    private final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private IndicatorAdapter mMagicIndicatorAdapter;
    private ViewPager mContentPager;
    private RoundRectIamgeView mRoundRectIamgeView;
    private ImageView mControlBtn;
    private TextView mSubTitle;
    private TextView mHeadTitle;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayerPanel;
    private View mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置状态栏颜色
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        initView();
        initPresenter();
        initEvent();
        //

    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
    }

    private void initEvent() {
        mMagicIndicatorAdapter.setOnIndicatorTabClicklistener(new IndicatorAdapter.OnIndictorTapbClickListene() {
            @Override
            public void onTabClick(int index) {
                if(mContentPager != null){
                    mContentPager.setCurrentItem(index);
                }
            }
        });

        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    if (!mPlayerPresenter.hasPlayList()) {
                        //没有播放列表，默认第一个第一首歌
                        playFirstRecommend();
                    }else{
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        }else{
                            mPlayerPresenter.play();
                        }
                    }

                }

            }
        });
        mPlayerPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到播放器页面
                if (mPlayerPresenter != null) {
                    if (!mPlayerPresenter.hasPlayList()) {
                        playFirstRecommend();
                    }
                    startActivity(new Intent(MainActivity.this, PlayerActivity.class));
                }

            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 播放第一个播放内容
     */
    private void playFirstRecommend() {
        LogUtil.d(TAG, "playFirstRecommend");
        RecommendPresenter recommendPresenter = RecommendPresenter.getInstance();
        List<Album> currentRecommend = recommendPresenter.getCurrentRecommend();
        if (currentRecommend != null) {
            Album album =currentRecommend.get(0);
            Long albumId = album.getId();
            mPlayerPresenter.playByAlbum(albumId);
        }



    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建适配器
        mMagicIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mMagicIndicatorAdapter);


        //ViewPager
        mContentPager = findViewById(R.id.vp_content);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mMainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mMainContentAdapter);
        //把ViewPager和indicator绑定到一起
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);
        //播放控制相关
        mPlayerPanel = findViewById(R.id.playerPanel);
        mRoundRectIamgeView = findViewById(R.id.iv_track_cover);
        mHeadTitle = findViewById(R.id.tv_track_title);
        mHeadTitle.setSelected(true);
        mSubTitle = findViewById(R.id.tv_sub_title);
        mControlBtn = findViewById(R.id.iv_play_control);
        //搜索相关
        mSearchBtn = findViewById(R.id.search_btn);

    }

    @Override
    public void onPlayStart() {
       updatePlayControl(true);
    }

    private void updatePlayControl(boolean isPlaying) {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(isPlaying?R.drawable.selector_player_stop:R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
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

    }



    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if(track != null){
            String trackTitle = track.getTrackTitle();
            String nickName = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (mHeadTitle != null) {
                mHeadTitle.setText(trackTitle);
            }
            if (mSubTitle != null) {
                mSubTitle.setText(nickName);
            }
            Picasso.with(this).load(coverUrlMiddle).into(mRoundRectIamgeView);
        }
    }

    @Override
    public void updateListOrder(boolean isRevers) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayerPresenter != null){
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }
}
