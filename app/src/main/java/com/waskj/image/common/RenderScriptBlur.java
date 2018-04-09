package com.waskj.image.common;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

/**
 * RenderScript高斯模糊
 * Created by Administrator on 2016/10/3 0003.
 */
public class RenderScriptBlur {
    /**
     * 执行高斯模糊
     *
     * @param bitmap
     * @param context
     * @return
     */
    public static Bitmap blurBitmap(Bitmap bitmap, Context context) {
        // 用需要创建高斯模糊bitmap创建一个空的bitmap
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // 初始化Renderscript，这个类提供了RenderScript context，
        // 在创建其他RS类之前必须要先创建这个类，他控制RenderScript的初始化，资源管理，释放
        RenderScript rs = RenderScript.create(context);

        // 创建高斯模糊对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 创建Allocations，此类是将数据传递给RenderScript内核的主要方法，
        // 并制定一个后备类型存储给定类型
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        // 设定模糊度
        blurScript.setRadius(18.f);

        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        // recycle the original bitmap
        bitmap.recycle();

        // After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return outBitmap;
    }

    public static void blurImage(Context context, ImageView blurImage){
        Bitmap bitmap= RenderScriptBlur.blurBitmap(ScreenShot.takeScreenShot((Activity)context), context);
        blurImage.setAlpha(0f);
        blurImage.setImageBitmap(bitmap);
        ObjectAnimator.ofFloat(blurImage, "alpha", 0f, 1f).setDuration(800).start();
    }

    public static void clearImageBlur(ImageView blurImage){
        ObjectAnimator.ofFloat(blurImage, "alpha", blurImage.getAlpha(), 0f).setDuration(500).start();
    }
}
