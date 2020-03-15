package com.zhangtao.himalaya.interfaces;

import com.zhangtao.himalaya.base.IBaesPresenter;

/**
 * presenter层
 */
public interface IAlbumDetialPresenter extends IBaesPresenter<IAlbumDetailViewCallback> {

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMores();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId, int page);
}
