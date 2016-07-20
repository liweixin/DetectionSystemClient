package com.prp.detectionsystemclient.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by liweixin on 2016/7/19.
 */

public class ViewPagerEx extends ViewPager{
    public ViewPagerEx(Context context){
        super(context);
    }
    public ViewPagerEx(Context context, AttributeSet set){
        super(context, set);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        return false;
    }
}
