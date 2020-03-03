package com.zhangtao.himalaya.interfaces;

/**
 * presenter层
 */
public interface IAlbumDetialPresenter {

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

    /**
     * 注册UI接口回调
     * @param callback
     */
    void registerViewCallback(IAlbumDetailViewCallback callback);

    /**
     * 取消UI接口回调
     * @param callback
     */
    void unRegisterViewCallback(IAlbumDetailViewCallback callback);
}
