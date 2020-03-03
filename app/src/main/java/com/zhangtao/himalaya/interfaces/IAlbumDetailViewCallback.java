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
}
