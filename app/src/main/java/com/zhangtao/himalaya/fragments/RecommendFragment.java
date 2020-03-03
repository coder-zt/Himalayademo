package com.zhangtao.himalaya.fragments;


import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ximalaya.ting.android.opensdk.model.album.Album;

import com.zhangtao.himalaya.DetailActivity;
import com.zhangtao.himalaya.R;
import com.zhangtao.himalaya.adapters.RecommendListAdapter;
import com.zhangtao.himalaya.base.BaseFragment;
import com.zhangtao.himalaya.interfaces.IRecommendPresenter;
import com.zhangtao.himalaya.interfaces.IRecommendViewCallback;
import com.zhangtao.himalaya.presents.AlbumDetailPresenter;
import com.zhangtao.himalaya.presents.RecommendPresenter;

import com.zhangtao.himalaya.utils.LogUtil;
import com.zhangtao.himalaya.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.OnItemClickListener {

    private final String TAG = "RecommendFragment";

    private View rootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter recommendListAdapter;
    private IRecommendPresenter mRecommendPresenter;
    private UILoader mUILoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater inflater, ViewGroup container) {
        mUILoader = new UILoader(getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(inflater, container);

                }
        };
        //获取到逻辑层对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //注册通知接口
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();


        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }
        mUILoader.setOnRetryClickListener(this);
        return mUILoader;
}

    private View createSuccessView(LayoutInflater inflater, ViewGroup container) {
        //view加载完成
        rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
        //RecyclerView的使用
        //1.找到控件
        mRecommendRv = rootView.findViewById(R.id.recommend_list);
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom =UIUtil.dip2px(view.getContext(), 5);
                outRect.left =UIUtil.dip2px(view.getContext(), 5);
                outRect.right =UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //3.设置适配器
        recommendListAdapter = new RecommendListAdapter();
        recommendListAdapter.setOnItemClickListener(this);
        mRecommendRv.setAdapter(recommendListAdapter);

        return  rootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当获取数据成功后，该方法被调用
        //得到数据后更新UI
        //把数据设置给适配器
        recommendListAdapter.setData(result);
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);

    }

    @Override
    public void onEmpty() {
        mUILoader.updateStatus(UILoader.UIStatus.EMPTY);

    }

    @Override
    public void onLoading() {
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册
        if(mRecommendPresenter != null){
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //网络不佳后的重试
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int index, Album album) {
        //item被点击了
        LogUtil.d(TAG, "第"+ index + "个被点击");
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //跳转至详情页面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
