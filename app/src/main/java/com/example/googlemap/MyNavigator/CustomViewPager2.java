package com.example.googlemap.MyNavigator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager2 extends ViewPager {
    private boolean isSwipeEnabled = false;

    public CustomViewPager2(Context context) {
        super(context);
    }

    public CustomViewPager2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSwipeEnabled(boolean enabled) {
        this.isSwipeEnabled = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSwipeEnabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isSwipeEnabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }
}
