package com.zhangtao.himalaya.fragments;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.zhangtao.himalaya.R;
import com.zhangtao.himalaya.adapters.RecommendListAdapter;
import com.zhangtao.himalaya.base.BaseFragment;
import com.zhangtao.himalaya.interfaces.IRecommendViewCallback;
import com.zhangtao.himalaya.presents.RecommendPresenter;
import com.zhangtao.himalaya.utils.Constants;
import com.zhangtao.himalaya.utils.LogUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {

    private final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPresenter mRecommendPresenter;


    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
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
        mRecommendRv.setAdapter(recommendListAdapter);
        //获取到逻辑层对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //注册通知接口
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();
        return rootView;
    }



    private void getUpRecommendUI(List<Album> albums) {
        LogUtil.d(TAG, "albums" + albums);


    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当获取数据成功后，该方法被调用
        //得到数据后更新UI
        //把数据设置给适配器
        recommendListAdapter.setData(result);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册
        if(mRecommendPresenter != null){
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }
}
