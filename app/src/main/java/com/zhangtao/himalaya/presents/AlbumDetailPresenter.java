package com.zhangtao.himalaya.presents;

import android.util.Log;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.zhangtao.himalaya.adapters.MainContentAdapter;
import com.zhangtao.himalaya.interfaces.IAlbumDetailViewCallback;
import com.zhangtao.himalaya.interfaces.IAlbumDetialPresenter;
import com.zhangtao.himalaya.utils.Constants;
import com.zhangtao.himalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.http2.ErrorCode;

public class AlbumDetailPresenter implements IAlbumDetialPresenter {

    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private final String TAG = "AlbumDetailPresenter";

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

    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        //根据Id和page获取详情类容
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "List size --->" + tracks.size());


                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "error ccode ---->" + errorCode);
                LogUtil.d(TAG, "errorMsg--->" + errorMsg);
            }
        });
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
