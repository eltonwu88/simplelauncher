package com.example.simplelauncher;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 解决Photo view 缩小时会报错的问题
 */
public class PhotoViewPager extends ViewPager {
    public PhotoViewPager(Context context) {
        super(context);
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try{
            return super.onInterceptTouchEvent(ev);
        }catch (IllegalArgumentException e){
            Log.i("Error",e.getMessage());
            return false;
        }
    }
}
