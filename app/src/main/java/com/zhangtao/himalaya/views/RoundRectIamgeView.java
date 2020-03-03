package com.zhangtao.himalaya.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;


public class RoundRectIamgeView extends AppCompatImageView{
    private static float roundRatio = 0.2f;
    private Path path;
    private final String TAG = "RoundRectIamgeView";

    public RoundRectIamgeView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(path == null){
            path = new Path();
            path.addRoundRect(new RectF(0,0,getWidth(), getHeight()), roundRatio*getWidth(), roundRatio*getHeight(), Path.Direction.CW);
        }
        canvas.save();
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();
    }
}
