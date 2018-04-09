package com.waskj.image.common;

/**
 * Created by fei on 2016/8/30 0030.
 */

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

    private ScrollViewListener scrollViewListener = null;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    /**
     * 到达两端时仍然可以滑动，提高用户体验
     */
    float x=0, y=0, v=0;
    VelocityTracker velocityTracker = VelocityTracker.obtain();
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        velocityTracker.clear();

        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(getScrollY() == 0 || getChildAt(0).getMeasuredHeight() == getScrollY() + getHeight()){
                    velocityTracker.addMovement(ev);
                    velocityTracker.computeCurrentVelocity(1000);
                    v = velocityTracker.getYVelocity();

                    //Log.e(TAG, "x= " + ev.getX() + "   y= " + ev.getY() + "   v= " + v);
                    float rate = (ev.getX() - x)/(ev.getY() - y);
                    float diff = ev.getY() - y;
                    //Log.e(TAG, "x= " + ev.getX() + "   diff= " + diff + "   getChildAt(0).getScrollY() = " + getScrollY() + "   v= " + v);
                    if(rate > -0.5f && rate < 0.5f && Math.abs(diff) < 80* Math.abs(v)/2000){
                        getChildAt(0).setTranslationY(getChildAt(0).getTranslationY() + diff/3);
                    }
                }
                x = ev.getX();
                y = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(getChildAt(0).getTranslationY() != 0f)
                    ObjectAnimator.ofFloat(getChildAt(0), "translationY", getChildAt(0).getTranslationY(), 0f).setDuration(300).start();
                    this.smoothScrollTo(0, 0);
                break;

        }
        return true;
    }

}
