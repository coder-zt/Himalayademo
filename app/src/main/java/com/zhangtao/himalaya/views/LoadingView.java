package com.zhangtao.himalaya.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.zhangtao.himalaya.R;
import com.zhangtao.himalaya.utils.LogUtil;


@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView {
    private  final String TAG = "LoadingView";
    //旋转角度
    private int rotateDegree = 0;
    private boolean isNeedRotate = false;


    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置图片
        setImageResource(R.mipmap.loading);
    }
    @Override
    protected void onAttachedToWindow() {
        LogUtil.d(TAG, "onAttachedToWindow...");
        super.onAttachedToWindow();
        isNeedRotate = true;
        //绑定到Window的时候
        post(new Runnable() {
            @Override
            public void run() {
                rotateDegree += 15;
                rotateDegree = rotateDegree<= 360?rotateDegree:0;
                invalidate();
                //是否继续旋转
//                LogUtil.d(TAG,  Thread.currentThread().getName());
                if (isNeedRotate) {
                    postDelayed(this, 100);
                }

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.d(TAG, "onDetachedFromWindow...");
        //当Window解绑了
        isNeedRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LogUtil.d(TAG, "onDraw...");
        /**
         * 1.旋转角度
         * 2.x的坐标
         * 3.y的坐标
         */
        canvas.rotate(rotateDegree, getWidth()/2, getHeight()/2);
        super.onDraw(canvas);
    }
}
