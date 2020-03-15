package com.zhangtao.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {
    /**
     * 专辑详情内容加载后
     * @param track
     */
    void onDetailListLoaded(List<Track> track);

    /**
     * 把album传到UI使用
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 当网络错误时的回调
     * @param errorCode
     * @param errorMsg
     */
    void onNetworkError(int errorCode, String errorMsg);

    /**
     * 加载更多的结果
     * @param size 加载的数量
     */
    void onLoadMoreFinished(int size);

    /**
     * 下拉加载更多的结果
     * @param size 加载数量
     */
    void onRefreshFinished(int size);
}
