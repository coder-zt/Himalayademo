package com.zhangtao.himalaya.api;

import android.net.wifi.aware.PublishConfig;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
import com.zhangtao.himalaya.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {
    private static XimalayaApi sXimalayaApi = null;
    private XimalayaApi(){}

    public static XimalayaApi getInstance(){
        if(sXimalayaApi == null){
            synchronized (XimalayaApi.class){
                if(sXimalayaApi == null){
                    sXimalayaApi = new XimalayaApi();
                }
            }
        }
        return sXimalayaApi;
    }
    /**
     * 获取推荐内容
     * @param callback 请求结果的回调
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callback){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, callback);
    }

    /**
     * 根据专辑的ID获取到专辑内容
     * @param callback 请求结果的回调
     * @param albumId 专辑ID
     * @param pageIndex 页数
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callback, long albumId, int pageIndex){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callback);
    }

    /**
     * 根据关键字的搜索
     * @param keyWord
     */
    public void searchByKeyword(String keyWord, int page ,IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyWord);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getSearchedAlbums(map, callback);
    }

    /**
     * 获取热词
     * @param callBack
     */
    public void getHotWords(IDataCallBack<HotWordList> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORLD + "");
        CommonRequest.getHotWords(map, callBack);
    }

    /**
     * 根据关键字获取联想词
     * @param keyword
     * @param callback
     */
    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callback);
    }
}
