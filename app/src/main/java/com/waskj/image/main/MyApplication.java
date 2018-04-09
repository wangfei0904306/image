package com.waskj.image.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.waskj.image.common.BitmapCache;


public class MyApplication extends Application {

	public static SharedPreferences sharedPrefs;
	public static Editor spEditor;
	private boolean firstStart = true;
	public static ImageLoader imageLoader;
	public static float density;
	public static int width;
	public static int height;

	@Override
	public void onCreate() {
		super.onCreate();
		sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		spEditor = sharedPrefs.edit();

		RequestQueue mQueue = Volley.newRequestQueue(this);
		imageLoader = new ImageLoader(mQueue, new BitmapCache());

		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		density = dm.density;
		width = dm.widthPixels;
		height = dm.heightPixels;
	}

	public boolean isFirstStart() {
		return firstStart;
	}

	public void setFirstStart(boolean firstStart) {
		this.firstStart = firstStart;
	}
}