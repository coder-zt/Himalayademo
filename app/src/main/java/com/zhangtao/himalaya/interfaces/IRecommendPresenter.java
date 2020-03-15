package com.zhangtao.himalaya.interfaces;

import com.zhangtao.himalaya.base.IBaesPresenter;

/**
 * presenter层
 */
public interface IRecommendPresenter extends IBaesPresenter<IRecommendViewCallback> {
    /**
     * 获取推荐内容
     */
    void getRecommendList();
}
