package com.waskj.image.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.waskj.image.R;
import com.waskj.image.common.MyScrollView;
import com.waskj.image.common.RenderScriptBlur;
import com.waskj.image.common.ScrollViewListener;

/**
 * Created by Administrator on 2016/8/24 0024.
 */
public class MainFragment extends Fragment implements ScrollViewListener {
    public static final String TAG = "MainFragment";

    public static final String INDEX = "current_index";
    public static final String SCROLL_Y = "share_scroll_y";
    public static final String TITLE = "frag_title";
    public static final String CONTENT = "frag_content";

    SharedPreferences.Editor spEditor;

    public static final String [] urls = {
            "http://image.tianjimedia.com/uploadImages/2013/297/QPD99T7KCO3D.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/072/37/F224Q74L6U5Q.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2016/336/38/R1Q89PJVOSZ1.jpg",
            "http://img.ugirls.tv/uploads/magazine/content/2f05ffc94859b3eca4e6353fcac2a2fa_magazine_web_m.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/071/14/NM1GDZ4YF2W0.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/079/38/N4V83NS9V3K7.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2017/263/46/E86ESVB68YZS.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2017/263/56/6WC19Z2YD5L2.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/092/37/A3178I07U8BA.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/092/23/C6Y3AYUNZFVN.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/092/51/5L12EAVM7655.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/089/21/FNVPU0W9EKK0.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/098/40/31O91NFE2R03.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/094/59/KGJA65197UOP.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/098/11/487176KJ7179.jpg",
            "http://dynamic-image.yesky.com/740x-/uploadImages/2018/093/47/2W5VCD62W49L.jpg"
    };

    long timer;

    View view;
    MyScrollView scroll;
    NetworkImageView fragImage;
    ImageView upTopImage;

    protected int index;
    protected int scrollY;

    Handler handler  = new Handler(Looper.getMainLooper());

    public static Fragment newInstance(int index, int title, int content){
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, index);
        bundle.putInt(TITLE, title);
        bundle.putInt(CONTENT, content);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spEditor = MyApplication.sharedPrefs.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main, container, false);
        scroll = (MyScrollView)view.findViewById(R.id.frag_scroll);
        scroll.setScrollViewListener(this);
        fragImage = view.findViewById(R.id.frag_image);
        upTopImage = (ImageView)view.findViewById(R.id.up_top_image);

        index = getArguments().getInt(INDEX, 0);
        fragImage.setImageUrl(urls[index],
                MyApplication.imageLoader);


        fragImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RenderScriptBlur.blurImage(getActivity(), MainActivity.blurImage);
            }
        });
        //fragImage.setScaleX(1.3f);
        //fragImage.setScaleY(1.3f);

        //向上滑动到顶按钮
        upTopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scroll.smoothScrollTo(0, 0);
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int y = MyApplication.sharedPrefs.getInt(SCROLL_Y + index, 0);
                Log.d(TAG, "Set ScrollY. index= " + index + "  scrollY= " + y );
                scroll.setScrollY(y);// 改变滚动条的位置
            }
        }, 50);

        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) fragImage.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = MyApplication.height;// 控件的宽
        fragImage.setLayoutParams(linearParams); //使设置好的布局参数应用到控件

    }

    @Override
    public void onPause(){

        super.onPause();
    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
        if(System.currentTimeMillis() - timer > 120){
            if(scrollY > y && View.INVISIBLE == upTopImage.getVisibility()){
                upTopImage.setVisibility(View.VISIBLE);
                handler.removeCallbacks(upTopRunnable);
                handler.postDelayed(upTopRunnable, 2500);
            }
            if(scrollY < y && View.VISIBLE == upTopImage.getVisibility())upTopImage.setVisibility(View.INVISIBLE);
            scrollY = y;
            handler.post(editorRunnable);
            timer = System.currentTimeMillis();
        }

    }

    /**
     * 存储当前浏览位置
     */
    Runnable editorRunnable = new Runnable() {
        @Override
        public void run() {
            spEditor.putInt(SCROLL_Y + index, scrollY);
            //Log.d(TAG, "Save Reading Position. SCROLL_Y + index=  " + SCROLL_Y + index + "  scrollY= " + scrollY );
            spEditor.commit();
        }
    };

    /**
     * 滑动到顶按钮的自动消失
     */
    Runnable upTopRunnable = new Runnable() {
        @Override
        public void run() {
            upTopImage.setVisibility(View.INVISIBLE);
        }
    };


}
