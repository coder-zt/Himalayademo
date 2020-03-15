package com.zhangtao.himalaya.interfaces;

import com.zhangtao.himalaya.base.IBaesPresenter;

public interface ISearchPresenter extends IBaesPresenter<ISearchCallBack> {

    /**
     * 搜索内容
     * @param keyWord
     */
    void doSearch(String keyWord);

    /**
     * 重新搜素
     */
    void reSearch();

    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWord();

    /**
     * 获取推荐关键词
     * @param keyWord
     */
    void getRecommendWorld(String keyWord);
}
