package com.waskj.image.main;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by fei on 2016/8/24 0024.
 */
public abstract class BaseActivity extends FragmentActivity {
    private static final int KEEP_SCREEN_ON = 1;
    private static final int NO_KEEP_SCREEN_ON = 0;
    private static final int DENY_SECONDS = 90; //屏幕常亮延迟的秒数

    private int screenStatus = 0;

    static final Handler handler  = new Handler();
    int downCount = DENY_SECONDS;
    Editor spEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        spEditor = MyApplication.sharedPrefs.edit();
    }

    @Override
    protected void onResume(){
        handler.postDelayed(countdownRunnable, 1000);
        super.onResume();
    }

    @Override
    protected void onPause(){
        handler.removeCallbacks(countdownRunnable);
        super.onPause();
    }

    float downX = 0, downY = 0;
    long doubleTimer = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN :
                downX = ev.getX();
                downY = ev.getY();
                downCount = DENY_SECONDS;
                //Log.v(this.getLocalClassName(), "screen alive deny for " + downCount);
                handler.postDelayed(longClickListenerRunnable, 500); //长按时间

                //double click
                //Log.e("TAG", "diff= "+(System.currentTimeMillis() - doubleTimer));
                if(System.currentTimeMillis() - doubleTimer < 350){
                    onDoubleClick();
                }
                doubleTimer = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(downX - ev.getX()) > 20 || Math.abs(downY - ev.getY()) >20){
                    handler.removeCallbacks(longClickListenerRunnable);
                    doubleTimer = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(longClickListenerRunnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取亮度
     * @param context
     * @return
     */
    float getLightness(Activity context) {
        WindowManager.LayoutParams localLayoutParams = context.getWindow().getAttributes();
        float light = localLayoutParams.screenBrightness;
        return light;
    }

    /**
     * 设置亮度
     * @param context
     * @param light
     */
    void setLight(Activity context, int light) {
        WindowManager.LayoutParams localLayoutParams = context.getWindow().getAttributes();
        localLayoutParams.screenBrightness = (light / 255.0F);
        context.getWindow().setAttributes(localLayoutParams);
    }

    /**
     * 倒计时
     */
    Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            downCount--;
            //Log.d("TAG", "................." + downCount);
            if(downCount > 0){
                addFlags();
            }else{
                //setLight(BaseActivity.this, 10);
                clearFlags();
            }
            handler.postDelayed(countdownRunnable, 1500);
        }
    };

    /**
     * 监听长按事件
     */
    Runnable longClickListenerRunnable = new Runnable() {
        @Override
        public void run() {
            onLongClick();
        }
    };

    /**
     * long click
     */
    public void onLongClick(){
        Log.v(this.getLocalClassName(), "long click base");
    }

    /**
     * long click
     */
    public void onDoubleClick(){
        Log.v(this.getLocalClassName(), "double click base");
    }

    /**
     * 屏幕保持常亮
     */
    public void addFlags(){
        if(screenStatus != KEEP_SCREEN_ON){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //屏幕常亮
            screenStatus = KEEP_SCREEN_ON;
        }
    }

    /**
     * 屏幕不保持常亮
     */
    public void clearFlags(){
        if(screenStatus != NO_KEEP_SCREEN_ON){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            screenStatus = NO_KEEP_SCREEN_ON;
        }
    }


}
