package com.waskj.image.common;

/**
 * Created by Administrator on 2016/10/3 0003.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenShot {
    public static final String TAG = "ScreenShot";

    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i(TAG, "Status Bar Height is " + statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Activity activity, int x, int y, int width, int height) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i(TAG, "Status Bar Height is " + statusBarHeight);

        // 获取屏幕长和高
        int winWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        int winHeight = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 校准标题栏
        y = y + statusBarHeight;

        Bitmap b = Bitmap.createBitmap(b1, x, y, width, height);
        view.destroyDrawingCache();
        return b;
    }

    // 保存到sdcard
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 程序入口
    public static void shoot(Activity a) {
        ScreenShot.savePic(ScreenShot.takeScreenShot(a), "sdcard/xx.png");
    }
}





//需要注意的是，shoot方法只能在view已经被加载后方可调用。
//
//        或者在    @Override
//public void onWindowFocusChanged(boolean hasFocus) {
//        // TODO Auto-generated method stub
//        super.onWindowFocusChanged(hasFocus);
//        ScreenShot.shoot(this);
//        }中调用
