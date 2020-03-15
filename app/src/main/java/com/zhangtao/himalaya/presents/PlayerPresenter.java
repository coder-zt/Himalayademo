package com.zhangtao.himalaya.presents;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.zhangtao.himalaya.api.XimalayaApi;
import com.zhangtao.himalaya.base.BaseApplication;
import com.zhangtao.himalaya.interfaces.IPlayerCallback;
import com.zhangtao.himalaya.interfaces.IPlayerPresenter;
import com.zhangtao.himalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();
    private final XmPlayerManager mXmPlayerManager;
    private static String TAG = "PlayerPresenter";
    private boolean isPlayListSet = false;
    private Track mCurrentTrack = null;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences mPlayMode;
    public static final int DEFAULT_PLAY_INDEX = 0;
    //SP’s name final
    static final String PLAY_MDOE_SP_NAME = "PlayMode";
    static final String PLAY_MDOE_SP_KEY = "PlayModeKey";
    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;
    private boolean mIsReverse = false;
    private int mCurrentProgressPosition = 0;
    private int mProgressDuration = 0;


    private PlayerPresenter(){
        mXmPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //注册广告相关的接口
        mXmPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
        mXmPlayerManager.addPlayerStatusListener(this);
        //记录当前的播放模式
        mPlayMode = BaseApplication.getAppContext().getSharedPreferences(PLAY_MDOE_SP_NAME, Context.MODE_PRIVATE);
    }

    private static PlayerPresenter sPlayerPresenter = null;

    public static PlayerPresenter getInstance(){
        if(sPlayerPresenter == null){
            synchronized (PlayerPresenter.class){
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    public void setPlayList(List<Track> list, int playIndex){
        if(mXmPlayerManager != null){
            LogUtil.d(TAG, "播放数据设置成功");
            mXmPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        }else{
            LogUtil.d(TAG, "播放数据为空");
        }
    }

    @Override
    public void play() {
        if (isPlayListSet) {
            mXmPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (isPlayListSet) {
            mXmPlayerManager.pause();
        }
    }

    @Override
    public void stop() {
        if (isPlayListSet) {
            mXmPlayerManager.stop();
        }
    }

    @Override
    public void playPre() {
        if (isPlayListSet) {
            mXmPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (isPlayListSet) {
            mXmPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.setPlayMode(mode);
            mCurrentMode = mode;
            //通知UI更新模式显示
            for(IPlayerCallback callback : mIPlayerCallbacks){
                callback.onPlayModeChange(mode);
            }
            //保存到SharePreferences中
            SharedPreferences.Editor editor = mPlayMode.edit();
            editor.putInt(PLAY_MDOE_SP_KEY, mode.ordinal());
            editor.apply();
            editor.commit();
        }
    }


    @Override
    public void seekTo(int progress) {
        //更新播放进度
        mXmPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        return mXmPlayerManager.isPlaying();
    }

    @Override
    public void getPlayList() {
        if (mXmPlayerManager != null) {
            LogUtil.d(TAG, "获取播放列表");
            List<Track> playList = mXmPlayerManager.getPlayList();
            for(IPlayerCallback callback : mIPlayerCallbacks){
                callback.onListLoaded(playList);
            }
        }

    }

    @Override
    public void playByIndex(int position) {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.play(position);
        }
    }

    @Override
    public void reversePlayList() {
        //把播放器列表反转
        List<Track> playList = mXmPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse =! mIsReverse;
        //第一个播放列表，第二个播放位置
        mCurrentIndex = playList.size()-mCurrentIndex - 1;
        LogUtil.d(TAG, "列表大小："+ playList.size() + "播放位置：" + mCurrentIndex + "mIsReverse is " + Boolean.toString(mIsReverse));
        mXmPlayerManager.setPlayList(playList,mCurrentIndex);
        //更新UI
        mCurrentTrack = (Track)mXmPlayerManager.getCurrSound();
        for(IPlayerCallback callback : mIPlayerCallbacks){
            callback.onListLoaded(playList);
            callback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            callback.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbum(long id) {
        // TODO: 2020/3/15
        //1.获取达到专辑的列表内容
        XimalayaApi.getInstance().getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                //2.把专辑内容设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && tracks.size() > 0) {
                    LogUtil.d(TAG,"已经设置了播放列表");
                    mXmPlayerManager.setPlayList(tracks, 0);
                    isPlayListSet = true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                    for(IPlayerCallback callback : mIPlayerCallbacks){
                        callback.onListLoaded(tracks);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(), "请求数据错误。。。", Toast.LENGTH_SHORT).show();
            }
        }, (int)id, 1);

        //3.播放音乐
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        //注册时更新标题
        iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
        iPlayerCallback.onProgressChange(mCurrentProgressPosition, mProgressDuration);
        //更新状态
        handlePlayState(iPlayerCallback);
        //从SharePerferences中拿去保存的数据
        mCurrentMode = XmPlayListControl.PlayMode.getIndex(mPlayMode.getInt(PLAY_MDOE_SP_KEY, PLAY_MODEL_LIST.ordinal()));
        iPlayerCallback.onPlayModeChange(mCurrentMode);
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
        if (isPlaying()) {
            iPlayerCallback.onPlayStart();
        }
    }

    private void handlePlayState(IPlayerCallback iPlayerCallback) {
        int playStatus = mXmPlayerManager.getPlayerStatus();
        //根据状态调用方法
        if (playStatus== PlayerConstants.STATE_STARTED) {
            iPlayerCallback.onPlayStart();
        }else{
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.remove(iPlayerCallback);
        }

    }
    //======================================广告物料的回调start=======================================//
        @Override
        public void onStartGetAdsInfo() {
            LogUtil.d(TAG, "onStartGetAdsInfo。。。");
        }

        @Override
        public void onGetAdsInfo(AdvertisList advertisList) {
            LogUtil.d(TAG, "onGetAdsInfo。。。");
        }

        @Override
        public void onAdsStartBuffering() {
            LogUtil.d(TAG, "onAdsStartBuffering。。。");

        }

        @Override
        public void onAdsStopBuffering() {
            LogUtil.d(TAG, "onAdsStopBuffering。。。");
        }

        @Override
        public void onStartPlayAds(Advertis advertis, int i) {
            LogUtil.d(TAG, "onStartPlayAds。。。");

        }

        @Override
        public void onCompletePlayAds() {
            LogUtil.d(TAG, "onCompletePlayAds。。。");
        }

        @Override
        public void onError(int what, int extra) {
            LogUtil.d(TAG, "onError。。。");
            LogUtil.d(TAG, "what-->" + what + "extra--->" + extra);
        }
    //======================================广告物料的回调end=======================================//
    //======================================播放器状态的回调start=======================================//
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG, "onPlayStart");
        for (IPlayerCallback playerCallback : mIPlayerCallbacks) {
            playerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG, "onPlayPause");
        for (IPlayerCallback playerCallback : mIPlayerCallbacks) {
            playerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG, "onPlayStop");
        for (IPlayerCallback playerCallback : mIPlayerCallbacks) {
            playerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG, "onSoundPlayComplete");

    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG, "onSoundPrepared");
        mXmPlayerManager.setPlayMode(mCurrentMode);
        if (mXmPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播放器准备完成，可以播放
            mXmPlayerManager.play();
        }

    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel currentMode) {
        mCurrentIndex = mXmPlayerManager.getCurrentIndex();
        LogUtil.d(TAG, "onSoundSwitch");
        if (currentMode instanceof Track) {
            Track currentTrack = (Track)currentMode;
            for( IPlayerCallback callback : mIPlayerCallbacks){
                callback.onTrackUpdate(currentTrack, mCurrentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG, "onBufferingStart");

    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG, "onBufferingStop");

    }

    @Override
    public void onBufferProgress(int i) {
        LogUtil.d(TAG, "onBufferProgress... ");

    }

    @Override
    public void onPlayProgress(int currentPos, int duration) {
        this.mCurrentProgressPosition = currentPos;
        this.mProgressDuration = duration;
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onProgressChange(currentPos, duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG, "onError error is " + e);
        return false;
    }

    public boolean hasPlayList() {
        return isPlayListSet;
    }
    //======================================播放器状态的回调end=======================================//
}
