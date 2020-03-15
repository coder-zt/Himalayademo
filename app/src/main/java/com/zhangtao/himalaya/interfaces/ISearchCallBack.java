package com.zhangtao.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallBack {

    /**
     * 搜索结果的回调方法
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取热词的结果的回调方法
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多结果的返回
     * @param result
     * @param isOkay
     */
    void onLoadMoreResult(List<Album> result,boolean isOkay);

    /**
     * 联想关键字的结果的回调方法
     * @param keyWorldList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWorldList);
}
