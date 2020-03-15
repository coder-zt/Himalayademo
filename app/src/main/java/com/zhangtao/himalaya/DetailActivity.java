package com.zhangtao.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhangtao.himalaya.adapters.DetailLisrAdatper;
import com.zhangtao.himalaya.base.BaseActivity;
import com.zhangtao.himalaya.base.BaseApplication;
import com.zhangtao.himalaya.interfaces.IAlbumDetailViewCallback;
import com.zhangtao.himalaya.interfaces.IPlayerCallback;
import com.zhangtao.himalaya.presents.AlbumDetailPresenter;
import com.zhangtao.himalaya.presents.PlayerPresenter;
import com.zhangtao.himalaya.utils.ImageBlur;
import com.zhangtao.himalaya.utils.LogUtil;
import com.zhangtao.himalaya.views.RoundRectIamgeView;
import com.zhangtao.himalaya.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailLisrAdatper.ItemClickListener, IPlayerCallback {

    private static final int DEFAULT_PLAY_INDEX = 0;
    private ImageView mLargeCover;
    private RoundRectIamgeView mSmallCover;
    private TextView mTextViewAlbumAuthor;
    private TextView mAlbumTitle;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private final String TAG = "DetailActivity";
    private int mCurrentPage = 1;
    private RecyclerView mDetailList;
    private DetailLisrAdatper mDetailListAdapter;
    private FrameLayout detailDataArea;
    private UILoader mUILoader;
    private int mCurrentId;
    private TextView mPlayControlTips;
    private ImageView mPlayControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = null;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean mLoaderMore = false;
    private String mCurrentTrackTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        //获取数据请求实例并注册回调
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        //播放器的实例与注册回调
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        updatePlayStatus(mPlayerPresenter.isPlaying());
        initListener();
    }



    private void initListener() {
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayerPresenter.hasPlayList()) {
                        //控制播放器播放状态
                        handlePlayControl();
                    }else {
                        handNotPlayList();
                    }

                }
            });
        }
    }

    /**
     * 播放器内容无
     */
    private void handNotPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);

    }

    private void initView() {
        //找到FrameLayout
        detailDataArea = this.findViewById(R.id.detail_data_area);
        //初始化UILoader
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return  createSuccessView(container);
                }
            };
            //===============================解析===============================
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            detailDataArea.removeAllViews();
            detailDataArea.addView(mUILoader);
            mUILoader.setOnRetryClickListener(this);
        }
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.viv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_title);
        mTextViewAlbumAuthor = this.findViewById(R.id.tv_author);
        mPlayControlBtn = this.findViewById(R.id.play_control_icon);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);
    }

    private View createSuccessView(ViewGroup container) {
       View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_detail_list, container, false);
        //刷新控件
        mRefreshLayout = successView.findViewById(R.id.refreshLayout);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getsHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BaseApplication.getAppContext(), "开始下拉刷新。。。", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //上拉加载更多内容
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    mLoaderMore = true;
                }
            }
        });
        mDetailList = successView.findViewById(R.id.album_detail_list);
        //RecyclerView的使用步骤
        //1.设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(linearLayoutManager);
        //2.设置适配器
        mDetailListAdapter = new DetailLisrAdatper();
        mDetailList.setAdapter(mDetailListAdapter);
        mDetailListAdapter.setmItemClickListener(this);
        //设置ITEM的间距
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        return successView;
    }

    @Override
    public void onDetailListLoaded(List<Track> track) {
        if(mLoaderMore && mRefreshLayout != null){
            mLoaderMore = false;
            mRefreshLayout.finishLoadmore();
        }

        this.mCurrentTracks = track;
        //判断数据结果，根据结果控制UI显示
        if(track == null || track.size() == 0){
            if(mUILoader != null){
                mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
            return;
        }
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //更新UI
        mDetailListAdapter.setData(track);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        //获取专辑的详情内容
        mCurrentId = (int)album.getId();
        mAlbumDetailPresenter.getAlbumDetail(mCurrentId, mCurrentPage);
        LogUtil.d(TAG, "Album加载完毕！");
        //正在加载详细数据
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        if(mAlbumTitle != null){
            mAlbumTitle.setText(album.getAlbumTitle());
        }

        if (mTextViewAlbumAuthor != null) {
            mTextViewAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }

        //毛玻璃效果
        if (mLargeCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {

                    Drawable drawable = mLargeCover.getDrawable();
                    if(drawable != null){//判断该控件是否已存在
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }
                @Override
                public void onError() {
                    LogUtil.d(TAG, "onError...");
                }
            });
        }

        if(mSmallCover != null){
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        //网络请求错误的回调
        if (mUILoader != null) {
           mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onLoadMoreFinished(int size) {
        if (size>0) {
            Toast.makeText(this, "成功加载" + size + "条节目", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "没有更多节目可供加载！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //网络不佳而点击的再次请求
        if(mAlbumDetailPresenter != null){
            mAlbumDetailPresenter.getAlbumDetail(mCurrentId, mCurrentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置要播放的数据
        LogUtil.d(TAG, "跳转并设置播放数据 position is " + position);
        PlayerPresenter playerPresenter = PlayerPresenter.getInstance();
        playerPresenter.setPlayList(detailData,position);
        //跳转至播放页面
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停文字为正在播放
        updatePlayStatus(true);

    }

    /**
     * 根据参数控制UI
     * @param playing
     */
    private void updatePlayStatus(boolean playing) {
        if (mPlayControlBtn != null && mPlayControlTips != null) {
            mPlayControlBtn.setImageResource(playing?R.drawable.selector_player_stop:R.drawable.selector_player_play);
            if (!playing) {
                if (!TextUtils.isEmpty(mCurrentTrackTitle)) {
                    mPlayControlTips.setText(mCurrentTrackTitle);
                }else{
                    mPlayControlTips.setText(R.string.click_play_tips_text);
                }
            }else{
                if (!TextUtils.isEmpty(mCurrentTrackTitle)) {
                    mPlayControlTips.setText(mCurrentTrackTitle);
                }
            }

        }
    }

    @Override
    public void onPlayPause() {
        //修改图标为播放文字为已暂停
        updatePlayStatus(false);
    }

    @Override
    public void onPlayStop() {
        //修改图标为播放文字为已暂停
        updatePlayStatus(false);
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
    public void onTrackUpdate(Track track, int palyIndex) {
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (TextUtils.isEmpty(mCurrentTrackTitle) || mPlayControlTips != null) {
                mPlayControlTips.setText(mCurrentTrackTitle);
            }
        }

    }

    @Override
    public void updateListOrder(boolean isRevers) {

    }

    private void handlePlayControl() {
            if(mPlayerPresenter.isPlaying()){
                mPlayerPresenter.pause();
            }else{
                mPlayerPresenter.play();
            }
    }
}
