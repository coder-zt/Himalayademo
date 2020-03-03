package com.zhangtao.himalaya;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhangtao.himalaya.base.BaseActivity;
import com.zhangtao.himalaya.interfaces.IAlbumDetailViewCallback;
import com.zhangtao.himalaya.presents.AlbumDetailPresenter;
import com.zhangtao.himalaya.utils.ImageBlur;
import com.zhangtao.himalaya.utils.LogUtil;
import com.zhangtao.himalaya.views.RoundRectIamgeView;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback {

    private ImageView mLargeCover;
    private RoundRectIamgeView mSmallCover;
    private TextView mTextViewAlbumAuthor;
    private TextView mAlbumTitle;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private final String TAG = "DetailActivity";
    private int mCurrentPage = 1;

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

    }

    private void initView() {
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.viv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_title);
        mTextViewAlbumAuthor = this.findViewById(R.id.tv_author);
    }

    @Override
    public void onDetailListLoaded(List<Track> track) {

    }

    @Override
    public void onAlbumLoaded(Album album) {
        //获取专辑的详细数据
        mAlbumDetailPresenter.getAlbumDetail((int)album.getId(), mCurrentPage);
        LogUtil.d(TAG, "Album加载完毕！");
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
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }
    }
}
