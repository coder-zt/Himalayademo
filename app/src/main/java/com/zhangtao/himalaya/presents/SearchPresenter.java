package com.zhangtao.himalaya.presents;

import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
import com.zhangtao.himalaya.api.XimalayaApi;
import com.zhangtao.himalaya.interfaces.ISearchCallBack;
import com.zhangtao.himalaya.interfaces.ISearchPresenter;
import com.zhangtao.himalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {
    private List<ISearchCallBack> mCallback = new ArrayList<>();
    //当前搜索的关键字
    private String mCurrentKeyWord = null;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private final XimalayaApi mXimalayaApi;
    private String TAG = "SearchPresenter";

    private SearchPresenter(){
        mXimalayaApi = XimalayaApi.getInstance();
    }
    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getInstance(){
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class){
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    @Override
    public void doSearch(String keyWord) {
        //用于重新搜索
        this.mCurrentKeyWord = keyWord;
        search(keyWord);

    }

    private void search(String keyWord) {
        mXimalayaApi.searchByKeyword(keyWord, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                LogUtil.d(TAG, "album size --->" + albums.size() );
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode--->" + errorCode + "errorMsg-->" + errorMsg);

            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyWord);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                List<HotWord>  hotWords = hotWordList.getHotWordList();
                LogUtil.d(TAG, "hotWords size--->" + hotWords.size());
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getHotWord errorCode--->" + errorCode);
                LogUtil.d(TAG, "getHotWord errorMsg--->" + errorMsg);

            }
        });
    }

    @Override
    public void getRecommendWorld(String keyWord) {
        mXimalayaApi.getSuggestWord(keyWord, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                List<QueryResult> keywordList = suggestWords.getKeyWordList();
                Log.d(TAG, "keywordList size---->" + keywordList.size());
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getRecommendWord errorCode--->" + errorCode);
                LogUtil.d(TAG, "getRecommendWord errorMsg--->" + errorMsg);

            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallBack iSearchCallBack) {
        if (!mCallback.contains(iSearchCallBack)) {
            mCallback.add(iSearchCallBack);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallBack iSearchCallBack) {
            mCallback.remove(iSearchCallBack);
    }
}
