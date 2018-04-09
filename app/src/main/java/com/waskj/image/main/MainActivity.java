package com.waskj.image.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.waskj.image.R;
import com.waskj.image.common.DragScaleView;
import com.waskj.image.common.MyFragmentPagerAdapter;
import com.waskj.image.common.RenderScriptBlur;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

    public static final int MAIN_TITLE_RESULT = 1;

    private ArrayList<Fragment> fragmentList;
    private ViewPager viewPager;
    public static ImageView blurImage;
    private DragScaleView shareDrag;

    public static final int CHAPTER_MIN = 0;
    public static int CHAPTER_MAX = 3;
    private int currIndex = 0;

    Handler handler  = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "-----------------onCreate---------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int day = MyApplication.sharedPrefs.getInt(Constants.DAY, 0);
        Log.d(TAG, "上次打开于" + day + "号   今天是" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "号");
        if(day != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            SplashActivity.actionStart(MainActivity.this);
        }

        CHAPTER_MAX = getResources().getInteger(R.integer.chapter_num);

        currIndex = MyApplication.sharedPrefs.getInt(Constants.CHAPTER, 0);
        initViewPager();

        blurImage = findViewById(R.id.image_blur_bg);
        shareDrag = findViewById(R.id.share_screen_shot);

    }

    @Override
    public void onPause(){
        super.onPause();
        if(shareDrag.getVisibility() == View.VISIBLE)
            shareDrag.setVisibility(View.GONE);
    }

    /**************初始化ViewPager,添加fragmentList***************/
    private void initViewPager() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentList = new ArrayList<>();

        for(int i = CHAPTER_MIN; i <= CHAPTER_MAX; i++){
            fragmentList.add(i, null);
        }
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setOffscreenPageLimit(1); //设置预加载数
        viewPagerSetCurrentItem(currIndex, false);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

    }

    private void viewPagerSetCurrentItem(int index, boolean isAnim){
        Log.d(TAG, "Set Current Index. index= " + index);
        fragListSetIndexItem(index -1, CHAPTER_MIN, CHAPTER_MAX);
        fragListSetIndexItem(index, CHAPTER_MIN, CHAPTER_MAX);
        fragListSetIndexItem(index+1, CHAPTER_MIN, CHAPTER_MAX);
        viewPager.setCurrentItem(index, isAnim);
    }

    /**
     * FRAG在最大和最小值之间才设置，否则不设置
     * @param index
     * @param min
     * @param max
     */
    private void fragListSetIndexItem(int index, int min, int max){
        if(index >= min && index <= max){
            int titleStrId;
            int contentStrId;
            titleStrId = getResources().getIdentifier("title" + index,"string", getPackageName());
            contentStrId = getResources().getIdentifier("content" + index,"string", getPackageName());
            fragmentList.set(index, MainFragment.newInstance(index, titleStrId, contentStrId));
        }
    }

    /*************** 选中页面后移动条动作到相应位置 **************/
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int page) {
            fragListSetIndexItem(page + (page - currIndex), CHAPTER_MIN, CHAPTER_MAX);
            currIndex = page;
            handler.post(editorRunnable);
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {  }
        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0){
                case 2:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_share_shutdown: {
                shareDrag.setVisibility(View.GONE);
                break;
            }
            default:
                break;
        }

    }

    Runnable editorRunnable = new Runnable() {
        @Override
        public void run() {
            spEditor.putInt(Constants.CHAPTER, currIndex);
            spEditor.commit();
        }
    };

    /**
     * long click
     */
    @Override
    public void onLongClick(){
        Log.v(TAG, "long click main");
        if(shareDrag.getVisibility() == View.VISIBLE)
            shareDrag.setVisibility(View.GONE);
        //RenderScriptBlur.blurImage(this, blurImage);
    }

    /**
     * double click
     */
    @Override
    public void onDoubleClick(){
        Log.v(TAG, "double click main");
        if(shareDrag.getVisibility() == View.GONE){
            shareDrag.setVisibility(View.VISIBLE);
        }else{
            shareDrag.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        RenderScriptBlur.clearImageBlur(blurImage);

        switch(requestCode){
            case MAIN_TITLE_RESULT:
                if(resultCode == RESULT_OK){
                    final int index = data.getIntExtra("index", 0);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPagerSetCurrentItem(index, true);
                        }
                    }, 200/abs(index-currIndex));
                    Log.d(TAG, "MainActiviey onActivityResult.  index= " + index);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 要做除数，不返回0值
     * @param temp
     * @return
     */
    private int abs(int temp){
        if(temp < 0){
            return -temp;
        }else if(temp == 0){
            return 1;
        }else {
            return temp;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return superOnKeyDown(keyCode);
    }

    //退出程序提示
    public boolean superOnKeyDown(int keyCode) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if(shareDrag.getVisibility() == View.VISIBLE){
                shareDrag.setVisibility(View.GONE);
                return true;
            }else{
                //return false;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("确认退出？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        }
        return false;
    }

}
