package com.zhangtao.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhangtao.himalaya.base.IBaesPresenter;

public interface IPlayerPresenter extends IBaesPresenter<IPlayerCallback> {

    /**
     * 播放音乐
     */
    void play();

    /**
     * 暂停音乐
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 播放上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 切换播放模式
     * @param mode
     */
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 调整播放进度
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 是否播放
     * @return
     */
    boolean isPlaying();

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 播放指定位置的内容
     * @param position
     */
    void playByIndex(int position);

    /**
     * 反转播放器里面的内容
     */
    void reversePlayList();

    /**
     * 播放指定歌单的第一首
     * @param id
     */
    void playByAlbum(long id);
}
