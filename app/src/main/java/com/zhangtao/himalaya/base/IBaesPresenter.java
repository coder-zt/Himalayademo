package com.zhangtao.himalaya.base;

public interface IBaesPresenter<T> {
    /**
     * 注册UI接口回调
     * @param T
     */
    void registerViewCallback(T t);

    /**
     * 取消UI接口回调
     * @param T
     */
    void unRegisterViewCallback(T t);
}
