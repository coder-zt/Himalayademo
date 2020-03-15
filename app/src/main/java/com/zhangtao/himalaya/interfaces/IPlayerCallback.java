package com.zhangtao.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {

    /**
     * 开始播放
     */
    void onPlayStart();


    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     *播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 播放下一首
     */
    void nextPlay(Track track);

    /**
     * 播放上一首
     */
    void onPrePlay(Track track);

    /**
     * 播放列表数据加载完毕
     * @param list 播放器列表数据
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放模式发生变化
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条发生改变
     * @param currentProgress
     * @param total
     */
    void onProgressChange(int currentProgress, int total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdFinished();

    /**
     * 更新节目信息
     * @param track
     */
    void onTrackUpdate(Track track, int palyIndex);

    /**
     * 通知更换播放顺序的UI
     * @param isRevers
     */
    void updateListOrder(boolean isRevers);
}
