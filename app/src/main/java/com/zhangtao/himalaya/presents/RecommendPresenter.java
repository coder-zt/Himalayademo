package com.zhangtao.himalaya.presents;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.zhangtao.himalaya.interfaces.IRecommendPresenter;
import com.zhangtao.himalaya.interfaces.IRecommendViewCallback;
import com.zhangtao.himalaya.utils.Constants;
import com.zhangtao.himalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {
    private String TAG = "RecommendPresenter";
    private List<IRecommendViewCallback> mCallback = new ArrayList<>();
    private RecommendPresenter(){}

    public static RecommendPresenter sInstance = null;

    /**
     * 获取单例对象
     * @return
     */
    public static RecommendPresenter getInstance(){
        if(sInstance == null){
            synchronized (RecommendPresenter.class){
                if(sInstance == null){
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取推荐内容，--猜你喜欢
     */
    @Override
    public void getRecommendList() {
        //封装数据
        updateLoading();
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
            CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
                @Override
                public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                    //数据获取成功
                    if (gussLikeAlbumList != null) {
                        List<Album> albums = gussLikeAlbumList.getAlbumList();
                        if (albums != null) {
                            LogUtil.d(TAG, "SIZE ---> " + albums.size());
                        }
                        HandlerRecommendResult(albums);
                    }
                }
                @Override
                public void onError(int i, String s) {
                    //数据获取失败
                    LogUtil.d(TAG,"error --> " + i);
                    LogUtil.d(TAG,"errorMsg --> " + s);
                    handlerError();
                }
            });
        }

    private void handlerError() {
        for(IRecommendViewCallback callback : mCallback){
            callback.onNetworkError();
        }
    }

    private void HandlerRecommendResult(List<Album> albums) {
        if (albums != null) {
            if(albums.size() == 0){
                for(IRecommendViewCallback callback: mCallback){
                    callback.onEmpty();
                }
            } else {
                //通知UI更新
                if (mCallback != null) {
                    for (IRecommendViewCallback callback : mCallback) {
                        callback.onRecommendListLoaded(albums);
                    }
                }
            }
        }
    }

    private void updateLoading(){
        for(IRecommendViewCallback callback : mCallback){
            callback.onLoading();
        }
    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if(mCallback != null && !mCallback.contains(callback)){
            mCallback.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if(mCallback != null){
            mCallback.remove(callback);
        }
    }
}

