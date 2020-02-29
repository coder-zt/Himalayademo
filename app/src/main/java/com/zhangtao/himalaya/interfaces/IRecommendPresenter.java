package com.zhangtao.himalaya.interfaces;

/**
 * presenter层
 */
public interface IRecommendPresenter {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    /**
     * 取消UI的回调注册
     * @param callback
     */
    void unRegisterViewCallback(IRecommendViewCallback callback);
}
