package com.zhangtao.himalaya.presents;

import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.zhangtao.himalaya.api.XimalayaApi;
import com.zhangtao.himalaya.base.BaseApplication;
import com.zhangtao.himalaya.interfaces.IAlbumDetailViewCallback;
import com.zhangtao.himalaya.interfaces.IAlbumDetialPresenter;
import com.zhangtao.himalaya.utils.Constants;
import com.zhangtao.himalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailPresenter implements IAlbumDetialPresenter {

    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();
    private final String TAG = "AlbumDetailPresenter";
    private int mCurrenAlbumId = -1;
    private int mCurrentPage = 0;

    private AlbumDetailPresenter(){}

    private  static AlbumDetailPresenter sInstance = null;

    public  static  AlbumDetailPresenter getInstance(){
        if(sInstance == null){
            synchronized (AlbumDetailPresenter.class){
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMores() {

    }

    @Override
    public void loadMore() {
        mCurrentPage++;
        doLoad(true);
    }

    private void doLoad(final boolean isLoadMore){
        //当前根据Id和page获取详情类容
        XimalayaApi ximalayaAp = XimalayaApi.getInstance();
        ximalayaAp.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    if (isLoadMore) {
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoadMoreResult(size);
                    }else{
                        mTracks.addAll(0,tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoadMore) {
                    mCurrentPage--;
                }
                LogUtil.d(TAG, "error ccode ---->" + errorCode);
                LogUtil.d(TAG, "errorMsg--->" + errorMsg);
                hanlderError(errorCode, errorMsg);
            }
        }, mCurrenAlbumId, mCurrentPage);
    }

    private void handlerLoadMoreResult(int size) {
        for (IAlbumDetailViewCallback viewCallback : mCallbacks) {
            viewCallback.onLoadMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrenAlbumId = albumId;
        this.mCurrentPage = page;
        doLoad(false);

    }

    private void hanlderError(int errorCode, String errorMsg) {
        for(IAlbumDetailViewCallback call : mCallbacks){
            if(call != null){
                call.onNetworkError(errorCode, errorMsg);
            }
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        //更新UI
        for(IAlbumDetailViewCallback call : mCallbacks){
            if(call != null){
                call.onDetailListLoaded(tracks);
            }
        }

    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback callback) {
        LogUtil.d(TAG,"注册IAlbumDetailViewCallback成功" + mTargetAlbum.getAlbumTitle());
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
            if (mTargetAlbum != null) {
                callback.onAlbumLoaded(mTargetAlbum);
            }else{
                LogUtil.d(TAG,"数据为空");
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback callback) {
        if (mCallbacks.contains(callback)) {
            mCallbacks.remove(callback);
        }
    }

    public void setTargetAlbum(Album targetAlbum){

        LogUtil.d(TAG,"设置数据成功" + targetAlbum.getAlbumTitle());
        mTargetAlbum = targetAlbum;
    }
}
